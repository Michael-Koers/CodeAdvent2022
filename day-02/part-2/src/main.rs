use std::fs;

#[derive(PartialEq, Eq, Clone, Copy)]
enum Shape {
    ROCK = 1,
    PAPER = 2,
    SCISSOR = 3,
}

#[derive(PartialEq, Eq, Clone, Copy)]
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
        let me: RoundResult = translate_string_to_result(split[1]);

        score += compute_result(opponent, me);
    }

    println!("My final score: {}", score);
}

fn compute_result(opponent: Shape, round_result: RoundResult) -> u32 {
    let shape: Shape = determine_shape(opponent, round_result);
    let shape_points = convert_shape_to_points(shape);

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

fn translate_string_to_result(result: &str) -> RoundResult {
    match result {
        "X" => return RoundResult::LOSE,
        "Y" => return RoundResult::DRAW,
        "Z" => return RoundResult::WIN,
        &_ => todo!("Default moet nog geimplementeerd worden"),
    }
}

fn determine_shape(opponent: Shape, me: RoundResult) -> Shape {
    // Basically the game logic
    match me {
        RoundResult::DRAW => return opponent,
        RoundResult::WIN => match opponent{
            Shape::ROCK => Shape::PAPER,
            Shape::PAPER => Shape::SCISSOR,
            Shape::SCISSOR => Shape::ROCK,
        },
        RoundResult::LOSE => match opponent {
            Shape::ROCK => Shape::SCISSOR,
            Shape::PAPER => Shape::ROCK,
            Shape::SCISSOR => Shape::PAPER,
        },
    }
}
