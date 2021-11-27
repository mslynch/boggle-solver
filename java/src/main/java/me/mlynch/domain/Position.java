package me.mlynch.domain;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class Position {

  private final int boardSize;
  @Getter private final int row;
  @Getter private final int column;

  public Stream<Position> getNeighborPositions() {
    return Stream.of(
            neighborAt(row - 1, column - 1),
            neighborAt(row - 1, column),
            neighborAt(row - 1, column + 1),
            neighborAt(row, column - 1),
            neighborAt(row, column + 1),
            neighborAt(row + 1, column - 1),
            neighborAt(row + 1, column),
            neighborAt(row + 1, column + 1))
        .filter(Position::isValid);
  }

  private Position neighborAt(final int row, final int column) {
    return new Position(boardSize, row, column);
  }

  private boolean isValid() {
    return (row >= 0 && row < boardSize && column >= 0 && column < boardSize);
  }
}
