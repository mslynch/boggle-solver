package me.mlynch.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import lombok.SneakyThrows;
import me.mlynch.domain.Word;

@Dependent
public class VocabularyConfig {

  @SneakyThrows
  @Produces
  List<Word> englishVocabulary() {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classloader.getResourceAsStream("vocabulary-english.txt");
    return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .map(Word::new)
        .collect(Collectors.toList());
  }
}
