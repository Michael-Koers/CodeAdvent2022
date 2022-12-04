use std::fs;

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

    // part 1 - let items = find_items(contents);
    // part 2
    let items = find_groups(contents);
    let count = count_items(items);

    println!("Total item count: {}", count);
}

fn find_items(contents: String) -> Vec<char> {
    let mut results = Vec::new();

    'outer: for line in contents.lines() {
        let (first, last) = line.split_at(line.len() / 2);

        for f in first.chars() {
            if last.contains(f) {
                results.push(f);
                continue 'outer;
            }
        }
    }

    return results;
}


fn find_groups(contents: String) -> Vec<char> {
    let mut results = Vec::new();


    // i = current index, item = current item
    // 'outer: for (i, item) in lines.enumerate(){
    //     for f in item.chars() {
    //         if lines.
    //     }
    // }
    // group 3 lines
    // find letter that is contained in each line
    // add to char vector
    // go to next line, that is 3 lines over, so +3

    return results;
}


fn count_items(items: Vec<char>) -> u32 {
    let mut count: u32 = 0;

    for item in items {
        if item.is_ascii_lowercase() {
            // minus 96 because 'a' is ascii code 97 in decimals, so to get 'a' as '1' we need to do -96
            count += (item as u32) - 96 
        } else if item.is_ascii_uppercase() {
            // same as above, but now 'A' is 65 in ascii and for this challange it is supposed to be 27, so minus -38
            count += (item as u32) - 38;
        } else {
            println!("Unexpected char: {}", item)
        }
    }

    return count;
}
