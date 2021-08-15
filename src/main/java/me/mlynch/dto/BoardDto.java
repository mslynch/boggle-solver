package me.mlynch.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {

  private List<List<String>> board;
}
