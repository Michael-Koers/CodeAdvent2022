use std::fs;
use std::ops::Range;

fn main() {
    let file_path: &str = "input.txt";
    println!("Reading file {}", file_path);

    let contents: String =
        fs::read_to_string(file_path).expect("Should have been able to read the file");

        check_overlap(contents);
}

fn check_overlap(contents : String){

    let mut line_count = 0;
    let mut contain_count = 0;
    let mut overlap_count = 0;

    for line in contents.lines() {

        let elf_ranges = line.split(",").collect::<Vec<&str>>();

        let elf_1_ranges = elf_ranges.get(0).unwrap().split("-").collect::<Vec<&str>>();
        let elf_2_ranges = elf_ranges.get(1).unwrap().split("-").collect::<Vec<&str>>();

        let elf_1_range = Range { start: elf_1_ranges.get(0).unwrap().parse::<u32>().unwrap(), end: elf_1_ranges.get(1).unwrap().parse::<u32>().unwrap()  };
        let elf_2_range = Range { start: elf_2_ranges.get(0).unwrap().parse::<u32>().unwrap(), end: elf_2_ranges.get(1).unwrap().parse::<u32>().unwrap()  };

        // part 1
        if fully_contains(&elf_1_range, &elf_2_range) {
            // println!("There ranges fully contain: {}..{} and {}..{}", elf_1_range.start, elf_1_range.end, elf_2_range.start, elf_2_range.end);
            contain_count += 1;
        }
        // part 2
        if overlap(&elf_1_range, &elf_2_range) {
            // println!("There ranges overlap: {}..{} and {}..{}", elf_1_range.start, elf_1_range.end, elf_2_range.start, elf_2_range.end);
            overlap_count += 1;
        }

        line_count += 1;
    }

    println!("Total lines: {}, contain count: {}, overlap count: {}", line_count, contain_count, overlap_count);

}

fn fully_contains(range1 : &Range<u32>, range2 : &Range<u32>) -> bool {
    (range1.start >= range2.start && range1.end <= range2.end) ||
        (range2.start >= range1.start && range2.end <= range1.end)
}

fn overlap(range1 : &Range<u32>, range2 : &Range<u32>) -> bool {
    // wtf? stolen from: https://stackoverflow.com/questions/3269434/whats-the-most-efficient-way-to-test-if-two-ranges-overlap
    range1.start <= range2.end && range1.end >= range2.start
}