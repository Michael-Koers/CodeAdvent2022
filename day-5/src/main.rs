use std::{fs, ops::Add};

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    let mut stacks: Vec<Vec<&str>> = Vec::new();

    // Starting positions
    stacks.push(vec!["V", "C", "D", "R", "Z", "G", "B", "W"]);  // 1
    stacks.push(vec!["G", "W", "F", "C", "B", "S", "T", "V"]);  // 2
    stacks.push(vec!["C", "B", "S", "N", "W"]);                 // 3
    stacks.push(vec!["Q", "G", "M", "N", "J", "V", "C", "P"]);  // 4
    stacks.push(vec!["T", "S", "L", "F", "D", "H", "B"]);       // 5
    stacks.push(vec!["J", "V", "T", "W", "M", "N"]);            // 6
    stacks.push(vec!["P", "F", "L", "C", "S", "T", "G"]);       // 7
    stacks.push(vec!["B", "D", "Z"]);                           // 8
    stacks.push(vec!["M", "N", "Z", "W"]);                      // 9


    println!("Start: {:?}", stacks);

    for line in contents.lines() {
        
        let splits = line.split_whitespace().collect::<Vec<&str>>();

        let mut amount = splits.get(1).unwrap().parse::<usize>().unwrap();
        
        // Adjust to array positions
        let from = splits.get(3).unwrap().parse::<usize>().unwrap() - 1;          
        let to = splits.get(5).unwrap().parse::<usize>().unwrap() - 1;

        // Part 1 solution
        // while amount > 0 {
            
        //     // We do it like this to prevent references to the stacks in the Vector, otherwise we run into
        //     // pointer-issues :)
        //     let pop = stacks.get_mut(from).unwrap().pop().unwrap();
        //     stacks.get_mut(to).unwrap().push(pop);
        //     amount -= 1;
        // }

        // Part 2 solution
        {
            
            let len: usize = stacks.get_mut(from).unwrap().len().clone();
            let pops = stacks.get_mut(from).unwrap().split_off(len - amount);
            stacks.get_mut(to).unwrap().extend(pops);
            
        }
    }

    println!("End: {:?}", stacks);

    let mut result: String = String::new();
    for stack in stacks {
        result = result.add(stack.last().unwrap());
    }

    println!("Result: {}", result);

}
