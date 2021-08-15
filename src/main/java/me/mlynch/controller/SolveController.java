package me.mlynch.controller;

import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import me.mlynch.domain.Board;
import me.mlynch.domain.Word;
import me.mlynch.dto.BoardDto;
import me.mlynch.dto.SolutionDto;
import me.mlynch.service.SolutionService;

@Path("/solve")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class SolveController {

  private final SolutionService solutionService;

  @POST
  public SolutionDto solve(BoardDto boardDto) {
    var board = new Board(boardDto.getBoard());
    var solution = solutionService.solve(board);
    var words =
        solution.getWords().stream().map(Word::getValue).sorted().collect(Collectors.toList());
    return new SolutionDto(solution.getScore(), words);
  }
}
