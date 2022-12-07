use std::{fs, collections::HashMap};

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    

    traverse_root(contents);

}

fn traverse_root(contents: String) -> HashMap<String, u32> {

    let lines: Vec<&str> = contents.lines().collect::<Vec<&str>>();
    let lines_incr = lines.iter().skip_while(|l| {println!("iterator: {}", l); l.starts_with("$")});
    let mut current_directory : &str = "";

    let directories : HashMap<String, u32> = HashMap::new();

    for (mut index, line) in lines_incr.enumerate() {

        // Only 'execute' commands
        if !line.starts_with("$"){
            continue;
        }

        match line {
            line if line.contains("ls") => {
                
                let mut inner_index = index.clone() + 1;
                while !lines.get(inner_index).unwrap().starts_with("$") {
                    println!("Found dir/file: {}", lines.get(inner_index).unwrap());
                    inner_index += 1;
                    
                }
            },
            line if line.contains("cd") => {
                println!("cmd: {} ", line);
                current_directory = line.split(" ").collect::<Vec<&str>>()[2];
                println!("Changed to dir {}", current_directory);
            },
             _line => {}
        }

    }

    return directories;

}

fn traverse_directory() -> (String, u32) {


    return (String::from("/"), 10);
}
