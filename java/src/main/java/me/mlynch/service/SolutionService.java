package me.mlynch.service;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import me.mlynch.domain.Board;
import me.mlynch.domain.Solution;
import me.mlynch.domain.Word;

@AllArgsConstructor
@ApplicationScoped
public class SolutionService {

  private final List<Word> englishVocabulary;

  public Solution solve(Board board) {
    var words = board.enumerateWords(englishVocabulary);
    var score = words.stream().mapToInt(Word::getScore).sum();
    return new Solution(score, words);
  }
}
