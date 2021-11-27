use axum::{extract::Extension, http::StatusCode, response::IntoResponse, Json};
use serde::{Deserialize, Serialize};
use std::sync::Arc;

use crate::solver::{Board, Word};

pub async fn solve(
    Json(board_request): Json<BoardRequest>,
    Extension(vocabulary): Extension<Arc<Vec<Word>>>,
) -> impl IntoResponse {
    let board = Board::new(&board_request.board);
    let mut solution = SolutionResponse::new();
    for word in board.enumerate_words(vocabulary.as_ref()).iter() {
        solution.words.push(word.value.clone());
        solution.score += word.score();
    }
    (StatusCode::OK, Json(solution))
}

#[derive(Serialize)]
pub struct SolutionResponse {
    words: Vec<String>,
    score: i32,
}

impl SolutionResponse {
    fn new() -> Self {
        Self {
            words: Vec::new(),
            score: 0,
        }
    }
}

#[derive(Deserialize)]
pub struct BoardRequest {
    board: Vec<Vec<String>>,
}
