mod route;
mod solver;

use axum::{routing::post, AddExtensionLayer, Router};
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::net::SocketAddr;
use std::sync::Arc;

use route::solve;
use solver::Word;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    let file = File::open("vocabulary-english.txt").expect("error reading vocabulary");
    let words: Vec<Word> = BufReader::new(file)
        .lines()
        .map(|line| Word::new(line.unwrap()))
        .collect();
    let vocabulary = Arc::new(words);

    let app = Router::new()
        .route("/solve", post(solve))
        .layer(AddExtensionLayer::new(vocabulary));

    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    tracing::info!("listening on {}", addr);
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}
