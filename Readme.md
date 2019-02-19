# Register Transfer Simulator
A digital logic simulator on register transfer level (RTL).

## Features
- Graphical user interface with move/delete/etc. functions
- Logic described using a hardware description language (similar to Verilog)
- Open/Save functions for your designs

## Planned Features
- Change hardware description to a popular one, probably Verilog (or a subset of Verilog)
- Probably remove arbitrary clock phases, just rising/falling
- HDL code generation (eg. for FPGA synthesis)
- Add oscilloscope view for simulation
- Import for existing memory content file formats

## Getting started
- Examples:
    - fibonacci.rtl: a very simple fibonacci sequence generator
    - SimpleCPU_branch_prediction.rtl: A simple pipelined CPU design
- Usage Canvas:
    - Add block: Right click on canvas -> Add ... block
    - Edit block properties: Double click on block
    - Move block: Drag block with mouse
    - Select items: Left click on block / drag rectangle selection starting on canvas / hold Ctrl to add to existing selection
    - Delete items: Select items, then press Del
    - Add wire: Drag mouse starting at a connector
    - Move connector: Select block, then drag connector
    - Add wire vertex: Right click on wire -> Add vertex
    - Remove wire vertex: Right click on vertex -> Delete Vertex
    - Add a display to wire: Right click on wire -> Add Wire Display
- Simulation:
    - Start simulation: Button 'Compile / Reset'
    - Advance one clock phase: Button 'Single Step'
    - Advance one full Clock cycle: Button 'Cycle'
- Logic Description:
    - Best look at the code the examples (until I write a tutorial)

