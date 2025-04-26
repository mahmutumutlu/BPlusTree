# CENG351 PA2 - B+ Tree Index

## Overview
This project implements a **B+ Tree** index system for storing academic papers. Two separate B+ Trees are used:

- **Primary B+ Tree** (Clustered Index): Indexed by `paperId`.
- **Secondary B+ Tree** (Unclustered Index): Indexed by `journal`.

The project supports adding, searching, and printing records. A provided GUI can be used for visualization, but grading will be based on non-GUI execution.

## Features
- **Add Paper**: Inserts a new paper into both primary and secondary B+ Trees.
- **Search Paper**: Searches for a paper by `paperId` in the primary B+ Tree.
- **Search Journal**: Searches for papers associated with a journal in the secondary B+ Tree.
- **Print Trees**: Displays the structure of primary and secondary B+ Trees in a depth-first order.

## Files
You should find the following implemented/modified files in this repository:
- `ScholarTree.java`
- `ScholarNodePrimaryIndex.java`
- `ScholarNodeSecondaryIndex.java`

The GUI-related classes (`CengGUI.java`, etc.) are provided but **not modified**.

## How to Compile and Run

### Compile all Java files
```bash
javac *.java
```
### Run the program

```bash
java CengScholar <order> <guiOptions> <inputFileName>
```

### Parameters

- `<order>`: Order (d) of the B+ trees (integer).

- `<guiOptions>`:
  - `0`: No GUI (default for grading)
  - `1`: Only Primary Tree GUI
  - `2`: Only Secondary Tree GUI
  - `3`: Both GUIs

- `<inputFileName>`: Input file name containing commands (`add`, `search1`, `search2`, `print1`, `print2`, `quit`).

##Example Usage
```bash
java CengScholar 2 0 sampleInput.txt
```
This command runs the program with tree order 2, no GUI, using `sampleInput.txt` as input.

### Input Commands

- `add|<paperId>|<journal>|<title>|<author>|`
- `search1|<paperId>`
- `search2|<journal>`
- `print1`
- `print2`
- `quit`

