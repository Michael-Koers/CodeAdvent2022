use std::{fs, collections::HashMap};

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    

    let result = traverse_root(contents);

    // println!("Eindresultaat: {:?}", result)
}

fn traverse_root(contents: String) -> HashMap<String, u32> {

    let lines: Vec<&str> = contents.lines().collect::<Vec<&str>>();
    let mut current_directory : String = String::from("");

    let mut directories : HashMap<String, u32> = HashMap::new();

    for (index, line) in lines.iter().enumerate() {

        // Only 'execute' commands
        if !line.starts_with("$"){
            continue;
        }

        println!("cwd: {}, line: {}", current_directory, line);

        match line {
            line if line.contains("ls") => {
                let mut dir_size : u32 = *directories.get(&current_directory).unwrap_or(&0);
                let mut inner_index = index.clone() + 1;
                
                while !lines.get(inner_index).unwrap_or(&"$").starts_with("$") {
                    
                    let cmd = lines.get(inner_index).unwrap().split(" ").collect::<Vec<&str>>();

                    if !cmd[0].starts_with("dir"){
                        dir_size += &dir_size + cmd[0].parse::<u32>().unwrap();
                    }
                    
                    inner_index += 1;
                }

                directories.insert(current_directory.clone(), dir_size);
            },
            line if line.contains("cd") => {
                let dir = line.split(" ").collect::<Vec<&str>>()[2];
                
                match dir {
                    dir if dir.contains("/") => { 
                        current_directory = "/".to_string();
                    },
                    dir if dir.contains("..") => {
                        let mut dirs = current_directory.split("/").collect::<Vec<&str>>();
                        dirs.pop();
                        current_directory = dirs.join("/");

                    }
                    dir => {
                        current_directory.push_str(dir);
                        current_directory.push_str("/");
                    }
                }
            },
             _line => {}
        }

    }

    return directories;

}