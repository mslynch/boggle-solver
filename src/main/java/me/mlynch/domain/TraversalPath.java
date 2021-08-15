package me.mlynch.domain;

import java.util.ArrayDeque;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TraversalPath {

  ArrayDeque<Cell> path = new ArrayDeque<>();

  public void push(Cell cell) {
    path.push(cell);
  }

  public void pop() {
    path.pop();
  }

  public Word getPathAsWord() {
    return new Word(
        StreamSupport.stream(
                Spliterators.spliterator(
                    path.descendingIterator(), path.size(), Spliterator.ORDERED),
                false)
            .map(Cell::getLetter)
            .collect(Collectors.joining()));
  }
}
