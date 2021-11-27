package me.mlynch.domain;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Solution {

  private final int score;
  private final Collection<Word> words;
}
