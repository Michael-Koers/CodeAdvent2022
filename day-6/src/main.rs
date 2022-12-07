use std::{collections::HashSet, fs};

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    let chars = contents.split("").filter(|l| !l.is_empty()).collect::<Vec<&str>>();

    println!("Line: {:?}", chars);

    for (index, _) in chars.iter().enumerate() {
        let mut set: HashSet<&str> = HashSet::new();

        // 0..3 is exclusive, =3 is inclusive end of range
        for r in 0..=3 {
            set.insert(chars[index + r]);
        }

        println!("Attempt {}: {:?}", index, set);

        if set.len() == 4 {
            println!("First marker after {}", index + 4);
            break;
        }
    }
}
