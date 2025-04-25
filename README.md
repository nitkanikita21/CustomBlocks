# CustomBlocksEngine (WIP)

Create custom blocks with plugins!

Examples of blocks [can be found here](src/main/java/me/nitkanikita21/customblocks/examples)

## Motivation

### The problem

As you know, plugins for Spigot/Paper are developed using the Bukkit API, which is an abstraction over the Minecraft
system. It's quite limited when it comes to adding new content to Minecraft, such as blocks or items. Unlike mods, which
work with the Minecraft system, plugins have to simulate blocks and items using the Bukkit API.

Currently, there are various ways and means of adding new blocks using plugins, but they all differ in one way or
another from what Mojang has implemented in Minecraft.

### Proposed solution

Using plugins, we need to simulate the operation of blocks. For minecraft systems, any block is still a regular
minecraft block, but we add our own logic of interaction, display, etc. to it.

My solution is not something radically new. I took the Minecraft block system and copied it for plugins. Thus,
we have an experience of creating blocks close to modding. The block system itself is quite scalable and flexible, it
allows you to create both simple and complex blocks

## Technical details

### How the blocks system works?

Just like in the Minecraft system, we have several main objects in the system:

1. `Registry<T>` - It is a universal repository of all content. There are several registries for each element of the
   system (Block, BlockEntityType, etc.)
2. `Block` - Stores general information about the block type, defines its parameters, display logic, provides initial
   parameters, etc.
3. `BlockEntity` - Defines the logic for each block in the world.BlockEntity is not required for every block, it may not
   exist for some type of block. This logic can include, for example, storing
   information about the block, or, for example, with the information of this block in the world. It can also provide a
   “ticker” - a method that will process this BlockEntity every tick of the world.
4. `BlockState` - Stores the parameters of the block. Unlike BlockEntity, BlockState always exists for every block in
   the world.
5. `BlockStateProperty<T>` - It is a block of the state that is spanned. For example, it can be “Facing” - where the
   block is facing.

### How does the system show custom blocks to the client?

It mainly depends on the block implementation and requirements.

Each block defines how it will be displayed to each client individually. Usually, custom blocks will be rendered as note
blocks combined with a resource pack to give them a unique texture. Each note is a separate texture.

For the server, each custom block is a note block, when the server sends packets to the player with blocks that are note
blocks, the server replaces the note block with the provided representation for each client from the custom block. Also,
each update of BlockState causes the block state to be updated for clients that see this custom block.

### How the system saves/loads all blocks after a server restart?

Each world has its own BlockManager. It processes and manipulates BlockStates and BlockEntities. When it is
necessary to save all the blocks for this world, a snapshot of this manager is generated. The snapshot contains all the
blocks installed in the world and the existing BlockEntities. Next, the snapshot writes all the information to the
provided NbtCompound, which in turn is saved to a file in the world folder. This happens for all worlds.
   
When we want to download all the blocks, we read these files and generate a new snapshot from the read information,
which we then apply to the corresponding BlockManager. Also, if BlockEntity provide “tickers”, they are launched for each
Blockchain after all the blocks are downloaded.


