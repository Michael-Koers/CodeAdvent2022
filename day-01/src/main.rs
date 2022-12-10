use std::fs;

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    let mut vec: Vec<u32> = Vec::new();
    let mut current_count: u32 = 0;
    let mut index: usize = 0;

    for line in contents.lines() {
        if line.is_empty() {
            vec.insert(index, current_count);
            index += 1;
            current_count = 0;
        } else {
            current_count += line.parse::<u32>().unwrap();
        }
    }

    vec.sort();
    vec.reverse();

    println!("Lijst: {:?}", vec);

    let total = vec[0] + vec[1] + vec[2];

    println!("Top 3 calories: {}", total);
}
