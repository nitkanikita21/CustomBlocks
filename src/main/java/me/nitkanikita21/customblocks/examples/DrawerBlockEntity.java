package me.nitkanikita21.customblocks.examples;

import lombok.Getter;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.blockstate.DefaultStateProperties;
import me.nitkanikita21.customblocks.core.util.Vector3iUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static me.nitkanikita21.customblocks.core.registry.BlockEntityTypes.DRAWER;

public class DrawerBlockEntity extends BlockEntity {
    private ItemDisplay itemDisplay;
    private TextDisplay textDisplay;
    private WorldAccessor accessor;

    public DrawerBlockEntity(Vector3i pos, BlockState state) {
        super(DRAWER, pos, state);
    }

    @Getter
    private ItemStack itemStack = ItemStack.empty();
    @Getter
    private int amount = 0;

    public void setAmount(int amount) {
        this.amount = amount;
        if(amount <= 0) {
            itemStack = ItemStack.empty();
        }
        checkSpawn();
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        if(itemStack.isEmpty()) {
            amount = 0;
        } else {
            amount = itemStack.getAmount();
        }
        checkSpawn();
    }

    private void checkSpawn() {
        BlockFace blockFace = getCachedState().getProperty(DefaultStateProperties.HORIZONTAL_FACING).get();

        Vector direction = blockFace.getDirection();

        Location bukkitLoc = Vector3iUtils.toLocation(accessor.getWorld(), getPos());

        if(amount > 0 && itemStack != null && !itemStack.isEmpty()) {


            if(itemDisplay == null && textDisplay == null) {
                itemDisplay = accessor.getWorld().spawn(
                    bukkitLoc,
                    ItemDisplay.class,
                    e -> {
                        e.teleport(
                            bukkitLoc.toCenterLocation()
                                .add(direction.multiply(0.51))
                                .setDirection(direction.multiply(-1))
                        );
                        e.setBillboard(Display.Billboard.FIXED);
                        e.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
                        e.setTransformation(
                            new Transformation(
                                new Vector3f(0f),
                                new AxisAngle4f(),
                                new Vector3f(0.5f, 0.5f, 0.01f),
                                new AxisAngle4f()
                            )
                        );
                        e.setBrightness(new Display.Brightness(0, 15));
                        e.setItemStack(itemStack);
                    }
                );
                textDisplay = accessor.getWorld().spawn(
                    bukkitLoc,
                    TextDisplay.class,
                    e -> {
                        e.teleport(
                            bukkitLoc.toCenterLocation()
                                .add(direction.multiply(-1.01).add(new Vector(0.0, -0.4, 0.0)))
                                .setDirection(blockFace.getDirection())
                        );
                        e.setBillboard(Display.Billboard.FIXED);
                        e.setAlignment(TextDisplay.TextAlignment.CENTER);
                        e.setShadowed(false);
                        e.setBackgroundColor(Color.fromARGB(0x00000000));

                        e.setTransformation(
                            new Transformation(
                                new Vector3f(0f),
                                new AxisAngle4f(),
                                new Vector3f(0.5f, 0.5f, 0.01f),
                                new AxisAngle4f()
                            )
                        );
                        e.text(Component.text(amount));
                    }
                );
            } else {
                textDisplay.text(Component.text(amount));
                itemDisplay.setItemStack(itemStack);
            }

        } else {
            destroyDisplays();
        }


    }

    private void destroyDisplays() {
        if(textDisplay != null) {
            textDisplay.remove();
            textDisplay = null;
        }
        if(itemDisplay != null) {
            itemDisplay.remove();
            itemDisplay = null;
        }
    }

    public void onPlaced(WorldAccessor accessor, BlockState state) {
        this.accessor = accessor;
        setCachedState(state);
        checkSpawn();
    }

    public void onDestroy() {
        destroyDisplays();
    }
}
