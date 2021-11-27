package me.mlynch.domain;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Word implements Comparable<Word> {

  @Getter private final String value;

  public int getScore() {
    switch (value.length()) {
      case 3:
      case 4:
        return 1;
      case 5:
        return 2;
      case 6:
        return 3;
      case 7:
        return 5;
      default:
        return 11;
    }
  }

  public boolean hasValidLength() {
    return value.length() > 2;
  }

  public boolean matches(Pattern pattern) {
    return pattern.matcher(value).find();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var word = (Word) o;
    return Objects.equals(value, word.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public int compareTo(Word o) {
    return this.value.compareTo(o.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
