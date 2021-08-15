package me.mlynch.domain;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class Vocabulary {

  private final PatriciaTrie<Word> words;

  public Vocabulary(final Stream<Word> words) {
    this.words =
        words.collect(
            Collectors.toMap(
                Word::getValue, Function.identity(), (w1, w2) -> w1, PatriciaTrie::new));
  }

  private Vocabulary(PatriciaTrie<Word> words) {
    this.words = words;
  }

  public Vocabulary subvocabularyWithPrefix(final Word prefix) {
    return new Vocabulary(new PatriciaTrie<>(words.prefixMap(prefix.getValue())));
  }

  public boolean isEmpty() {
    return words.isEmpty();
  }

  public boolean contains(final Word word) {
    return words.containsKey(word.getValue());
  }
}
