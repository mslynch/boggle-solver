use itertools::Itertools;
use regex::Regex;
use sequence_trie::SequenceTrie;
use std::cell::RefCell;
use std::collections::HashMap;
use std::collections::{BTreeSet, HashSet};
use std::hash::Hash;
use std::rc::Rc;

#[derive(Eq, PartialEq, Hash)]
pub struct Word {
    pub value: String,
}

impl Word {
    pub fn new(value: String) -> Self {
        Word { value }
    }

    fn matches(&self, regex: &Regex) -> bool {
        regex.is_match(&self.value)
    }

    pub fn score(&self) -> i32 {
        match self.value.len() {
            3 | 4 => 1,
            5 => 2,
            6 => 3,
            7 => 5,
            _ => 11,
        }
    }
}

impl PartialOrd for Word {
    fn partial_cmp(&self, other: &Self) -> std::option::Option<std::cmp::Ordering> {
        self.value.partial_cmp(&other.value)
    }
}

impl Ord for Word {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.value.cmp(&other.value)
    }
}

pub struct Board {
    cells: Vec<Rc<RefCell<BoardCell>>>,
    letter_set_pattern: Regex,
}

impl Board {
    pub fn new(letters: &[Vec<String>]) -> Self {
        let board_size = letters.len();

        let cells_by_position: HashMap<Position, Rc<RefCell<BoardCell>>> = (0..board_size)
            .flat_map(|row| {
                (0..board_size).map(move |col| {
                    let position = Position::new(row as i32, col as i32);
                    let letter = letters
                        .get(position.row as usize)
                        .unwrap()
                        .get(position.column as usize)
                        .unwrap()
                        .to_string();
                    BoardCell::new(position, letter)
                })
            })
            .map(|cell| (cell.position, Rc::new(RefCell::new(cell))))
            .collect();

        cells_by_position.values().for_each(|cell| {
            cell.replace_with(|board_cell| BoardCell {
                position: board_cell.position,
                letter: board_cell.letter.clone(),
                neighbors: board_cell.get_neighbors(&cells_by_position, board_size),
            });
        });

        let cells = cells_by_position
            .into_iter()
            .map(|(_position, cell)| cell)
            .collect();

        let letter_set_pattern = Regex::new(
            &letters
                .iter()
                .flat_map(|s| s.to_owned())
                .unique()
                .collect::<Vec<_>>()
                .join("|"),
        )
        .unwrap();

        Board {
            cells,
            letter_set_pattern,
        }
    }

    pub fn enumerate_words(self, vocabulary: &[Word]) -> BTreeSet<Word> {
        let mut vocab_structure: SequenceTrie<char, bool> = SequenceTrie::new();
        vocabulary
            .iter()
            .filter(|word| word.matches(&self.letter_set_pattern))
            .for_each(|word| {
                vocab_structure.insert_owned(word.value.chars(), true);
            });
        let vocabulary = Vocabulary::new(&vocab_structure);
        let mut visited = HashSet::new();
        let mut traversal_path = TraversalPath::new();
        let mut real_words = BTreeSet::new();
        self.cells.iter().for_each(|cell| {
            depth_first_traverse(
                cell,
                &mut visited,
                &mut traversal_path,
                &mut real_words,
                &vocabulary,
            );
        });
        real_words
    }
}

struct BoardCell {
    position: Position,
    letter: String,
    neighbors: Vec<Rc<RefCell<BoardCell>>>,
}

impl PartialEq for BoardCell {
    fn eq(&self, other: &BoardCell) -> bool {
        self.position == other.position
    }
}

impl Eq for BoardCell {}

impl Hash for BoardCell {
    fn hash<H>(&self, state: &mut H)
    where
        H: std::hash::Hasher,
    {
        self.position.hash(state)
    }
}

impl BoardCell {
    fn new(position: Position, letter: String) -> Self {
        Self {
            position,
            letter,
            neighbors: vec![],
        }
    }

    fn get_neighbors(
        &mut self,
        cells_by_position: &HashMap<Position, Rc<RefCell<BoardCell>>>,
        board_size: usize,
    ) -> Vec<Rc<RefCell<BoardCell>>> {
        self.position
            .get_neighbor_positions(board_size)
            .map(|position| {
                let neighbor_cell = cells_by_position.get(&position).unwrap();
                Rc::clone(neighbor_cell)
            })
            .collect()
    }
}

#[derive(Hash, Eq, PartialEq, Clone, Copy)]
struct Position {
    row: i32,
    column: i32,
}
impl Position {
    fn new(row: i32, column: i32) -> Self {
        Self { row, column }
    }

    fn get_neighbor_positions(&self, board_size: usize) -> impl Iterator<Item = Position> {
        vec![
            Position::new(self.row - 1, self.column - 1),
            Position::new(self.row - 1, self.column),
            Position::new(self.row - 1, self.column + 1),
            Position::new(self.row, self.column - 1),
            Position::new(self.row, self.column + 1),
            Position::new(self.row + 1, self.column - 1),
            Position::new(self.row + 1, self.column),
            Position::new(self.row + 1, self.column + 1),
        ]
        .into_iter()
        .filter(move |position| position.is_valid_for_size(board_size))
    }

    fn is_valid_for_size(&self, board_size: usize) -> bool {
        self.row >= 0
            && self.row < board_size as i32
            && self.column >= 0
            && self.column < board_size as i32
    }
}

fn depth_first_traverse(
    cell: &Rc<RefCell<BoardCell>>,
    visited: &mut HashSet<Position>,
    traversal_path: &mut TraversalPath,
    real_words: &mut BTreeSet<Word>,
    vocabulary: &Vocabulary,
) {
    let board_cell = cell.as_ref().borrow();
    let position = board_cell.position;
    if visited.contains(&position) {
        return;
    }
    visited.insert(position);
    traversal_path.push(cell);

    if let Some(subvocabulary) = vocabulary.subvocabulary_using_traversal_ending(traversal_path) {
        if traversal_path.is_long_enough_for_valid_word()
            && subvocabulary.current_node_is_word_end()
        {
            real_words.insert(traversal_path.to_word());
        }
        if subvocabulary.has_longer_words() {
            for neighbor in &board_cell.neighbors {
                depth_first_traverse(
                    neighbor,
                    visited,
                    traversal_path,
                    real_words,
                    &subvocabulary,
                );
            }
        }
    }

    traversal_path.step_out();
    visited.remove(&position);
}

struct Vocabulary<'a> {
    words: &'a SequenceTrie<char, bool>,
}

impl<'a> Vocabulary<'a> {
    fn new(words: &'a SequenceTrie<char, bool>) -> Self {
        Self { words }
    }

    fn subvocabulary_using_traversal_ending(&self, traversal_path: &TraversalPath) -> Option<Self> {
        self.words
            .get_node(traversal_path.last_chars().iter())
            .map(|subwords| Self { words: subwords })
    }

    fn current_node_is_word_end(&self) -> bool {
        self.words.value().is_some()
    }

    fn has_longer_words(&self) -> bool {
        !self.words.is_leaf()
    }
}

struct TraversalPath {
    path: Vec<char>,
}

impl TraversalPath {
    fn new() -> Self {
        TraversalPath { path: vec![] }
    }

    fn last_chars(&self) -> Vec<char> {
        let len = self.path.len();
        if len == 0 {
            vec![]
        } else if len == 1 {
            let ultimate_char = self.path[self.path.len() - 1];
            vec![ultimate_char]
        } else {
            let penultimate_char = self.path[self.path.len() - 2];
            if penultimate_char == 'q' {
                vec!['q', 'u']
            } else {
                let ultimate_char = self.path[self.path.len() - 1];
                vec![ultimate_char]
            }
        }
    }

    fn to_word(&self) -> Word {
        Word::new(self.path.iter().collect())
    }

    fn push(&mut self, board_cell: &Rc<RefCell<BoardCell>>) {
        let c = board_cell.as_ref().borrow().letter.chars().next().unwrap();
        self.path.push(c);
    }

    fn step_out(&mut self) {
        self.path.remove(self.path.len() - 1);
    }

    fn is_long_enough_for_valid_word(&self) -> bool {
        self.path.len() > 2
    }
}
