use std::fs;

#[derive(PartialEq, Eq, Clone, Copy)]
enum Shape {
    ROCK = 1,
    PAPER = 2,
    SCISSOR = 3,
}

enum RoundResult {
    WIN = 6,
    DRAW = 3,
    LOSE = 0,
}

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    let mut score: u32 = 0;

    for line in contents.lines() {
        let split = line.split(' ').collect::<Vec<&str>>();
        let opponent: Shape = translate_string_to_shape(split[0]);
        let me: Shape = translate_string_to_shape(split[1]);

        score += compute_result(opponent, me);
    }

    println!("My final score: {}", score);
}

fn compute_result(opponent: Shape, me: Shape) -> u32 {
    let shape_points = convert_shape_to_points(me);
    let round_result = determine_round_result(opponent, me);

    return shape_points + round_result as u32;
}

fn convert_shape_to_points(shape: Shape) -> u32 {
    return shape as u32;
}

fn translate_string_to_shape(shape: &str) -> Shape {
    match shape {
        "A" | "X" => return Shape::ROCK,
        "B" | "Y" => return Shape::PAPER,
        "C" | "Z" => return Shape::SCISSOR,
        &_ => todo!("Default moet nog geimplementeerd worden"),
    }
}

fn determine_round_result(opponent: Shape, me: Shape) -> RoundResult {
    match opponent {
        Shape::ROCK => match me {
            Shape::ROCK => RoundResult::DRAW,
            Shape::PAPER => RoundResult::WIN,
            Shape::SCISSOR => RoundResult::LOSE,
        },
        Shape::PAPER => match me {
            Shape::ROCK => RoundResult::LOSE,
            Shape::PAPER => RoundResult::DRAW,
            Shape::SCISSOR => RoundResult::WIN,
        },
        Shape::SCISSOR => match me {
            Shape::ROCK => RoundResult::WIN,
            Shape::PAPER => RoundResult::LOSE,
            Shape::SCISSOR => RoundResult::DRAW,
        },
    }
}
