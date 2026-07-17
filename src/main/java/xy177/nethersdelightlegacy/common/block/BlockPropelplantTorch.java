package xy177.nethersdelightlegacy.common.block;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;

public class BlockPropelplantTorch extends BlockTorch {
    public BlockPropelplantTorch() {
        setLightLevel(0.75F);
        setSoundType(SoundType.WOOD);
        setTickRandomly(false);
    }
}
