package me.mlynch.domain;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Board {

  private final Collection<Cell> cells;
  private final Pattern letterSetPattern;

  private static final Collector<CharSequence, ?, String> REGEX_JOINING_COLLECTOR =
      Collectors.joining("", "[", "]");

  public Board(List<List<String>> letters) {
    var boardSize = letters.size();
    var cellsByPosition =
        IntStream.range(0, boardSize)
            .mapToObj(
                row ->
                    IntStream.range(0, boardSize)
                        .mapToObj(column -> new Position(boardSize, row, column)))
            .flatMap(Function.identity())
            .map(
                position ->
                    new Cell(position, letters.get(position.getRow()).get(position.getColumn())))
            .collect(Collectors.toMap(Cell::getPosition, Function.identity()));
    cells = cellsByPosition.values();
    for (var cell : cells) {
      cell.populateNeighbors(cellsByPosition);
    }

    var letterSetRegex =
        letters.stream()
            .flatMap(List::stream)
            .map(String::toLowerCase)
            .distinct()
            .collect(REGEX_JOINING_COLLECTOR);
    letterSetPattern = Pattern.compile(letterSetRegex);
  }

  public Set<Word> enumerateWords(List<Word> vocabularyList) {
    var visited = new HashSet<Cell>();
    var realWords = new HashSet<Word>();
    var traversalPath = new TraversalPath();
    var vocabulary =
        new Vocabulary(vocabularyList.stream().filter(word -> word.matches(letterSetPattern)));
    for (var cell : cells) {
      depthFirstTraversal(cell, visited, traversalPath, realWords, vocabulary);
    }
    return realWords;
  }

  private void depthFirstTraversal(
      Cell cell,
      Set<Cell> visited,
      TraversalPath traversalPath,
      Set<Word> realWords,
      Vocabulary vocabulary) {
    if (!visited.contains(cell)) {
      visited.add(cell);
      traversalPath.push(cell);

      var wordCandidate = traversalPath.getPathAsWord();
      Vocabulary subvocabulary = vocabulary.subvocabularyWithPrefix(wordCandidate);
      if (wordCandidate.hasValidLength() && subvocabulary.contains(wordCandidate)) {
        realWords.add(wordCandidate);
      }
      if (!subvocabulary.isEmpty()) {
        for (var neighbor : cell.getNeighbors()) {
          depthFirstTraversal(neighbor, visited, traversalPath, realWords, subvocabulary);
        }
      }

      traversalPath.pop();
      visited.remove(cell);
    }
  }
}
