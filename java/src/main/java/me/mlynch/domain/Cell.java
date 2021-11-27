package me.mlynch.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Cell {

  private final Position position;
  private final String letter;
  private List<Cell> neighbors;

  public Cell(final Position position, final String letter) {
    this.position = position;
    this.letter = letter;
  }

  public void populateNeighbors(Map<Position, Cell> boardCells) {
    neighbors = position.getNeighborPositions().map(boardCells::get).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var cell = (Cell) o;
    return Objects.equals(position, cell.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position);
  }

  @Override
  public String toString() {
    return "Cell{position={"
        + position.getRow()
        + ","
        + position.getColumn()
        + "},letter="
        + letter
        + "}";
  }
}
