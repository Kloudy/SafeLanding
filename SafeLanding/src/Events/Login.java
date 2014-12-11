package Events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Login implements Listener
{
	@EventHandler
	public void LoginEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		if(player.hasPermission("safelanding.use"))
		{
			Location location = player.getLocation();
			World world = player.getWorld();
			
			Block currentBlock = world.getBlockAt(location.getBlockX(), location.getBlockY() -1, location.getBlockZ());
			
			//player joined the server while in mid-air
			if(currentBlock.getType() == Material.AIR || isDangerous(currentBlock) || player.getLocation().getY() >= 256 || player.getLocation().getY() <= 0)
			{			
				//check 11x11 column under the player for a safe landing. If none is found, tp them back to spawn
				int y = location.getBlockY();
				boolean safeLanding = false;
				boolean danger = false;
				
				if(y >= 256)
				{
					y = 255;
				}
				
				if(isDangerous(currentBlock))
				{
					danger = true;
				}
				
				Block safeBlock = null;
				
				while(y > 0 && !safeLanding)
				{
					for(int x = location.getBlockX() - 5; x <= location.getBlockX() + 5; x++)
					{
						if(safeLanding)
						{
							break;
						}
						
						for(int z = location.getBlockZ() - 5; z <= location.getBlockZ() + 5; z++)
						{
							currentBlock = world.getBlockAt(x, y, z);
	
							if(currentBlock.getType() != Material.AIR && !isDangerous(currentBlock))
							{
								//make sure there are at least two blocks of air above the block so the player doesn't suffocate
								if(world.getBlockAt(x, y+1, z).getType() == Material.AIR && world.getBlockAt(x, y+2, z).getType() == Material.AIR)
								{
									safeLanding = true;
									safeBlock = currentBlock;
									break;
								}
							}
							
							if(isDangerous(currentBlock))
							{
								danger = true;
							}
						}
					}
					
					y -= 1;
				}
				
				if(safeLanding && safeBlock != null)
				{
					player.teleport(new Location(world, safeBlock.getX() + 0.5, safeBlock.getY()+1, safeBlock.getZ() + 0.5));
					
					if(danger)
					{
						player.sendMessage(ChatColor.GREEN + "You logged in over an unsafe area. Teleported you to the nearest safe location.");
					}
				}
				else
				{
					player.teleport(world.getSpawnLocation());
					
					player.sendMessage(ChatColor.GREEN + "You logged in over an unsafe area and a nearby safe location couldn't be found. Teleported you back to spawn.");
				}
			}
		}
	}
	
	private boolean isDangerous(Block block)
	{
		return (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.CACTUS || block.getType() == Material.FIRE);
	}
}
