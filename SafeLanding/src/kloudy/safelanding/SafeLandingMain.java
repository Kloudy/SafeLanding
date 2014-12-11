package kloudy.safelanding;

import org.bukkit.plugin.java.JavaPlugin;

import Events.Login;

public class SafeLandingMain extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new Login(), this);
	}
	
	@Override
	public void onDisable()
	{
		
	}
}
