use std::{collections::HashSet, fs};

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    // for day 1 solution, use marker_length = 4
    // for day 2 solution, use marker_length = 14
    let marker_length = 14;

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    let chars = contents.split("").filter(|l| !l.is_empty()).collect::<Vec<&str>>();

    println!("Line: {:?}", chars);

    for (index, _) in chars.iter().enumerate() {
        let mut set: HashSet<&str> = HashSet::new();

        // 0..3 is exclusive, =3 is inclusive end of range
        for r in 0..marker_length {
            set.insert(chars[index + r]);
        }

        println!("Attempt {}: {:?}", index, set);

        if set.len() == marker_length {
            println!("First marker after {}", index + marker_length);
            break;
        }
    }
}
