package me.amfero.blmclient.module;

import java.util.ArrayList;
import java.util.stream.Collectors;

import me.amfero.blmclient.module.combat.*;
import me.amfero.blmclient.module.exploit.*;
import me.amfero.blmclient.module.hud.*;
import me.amfero.blmclient.module.misc.*;
import me.amfero.blmclient.module.movement.*;
import me.amfero.blmclient.module.render.*;

public class ModuleManager
{
	private final ArrayList<Module> modules = new ArrayList<>();
	
	public ModuleManager()
	{
		//Combat
		modules.add(new AntiEndCrystal());
		modules.add(new AutoArmor());
		modules.add(new AutoLog());
		modules.add(new AutoTotem());
		modules.add(new Criticals());
		//modules.add(new CrystalAura());
		modules.add(new KillAura());
		modules.add(new RightClickGap());
		modules.add(new Surround());
		//Exploit
		modules.add(new AntiHunger());
		modules.add(new InstantBurrow());
		//modules.add(new PacketMine());
		//modules.add(new PacketEat());
		modules.add(new QueueSkip());
		modules.add(new RockezFly());
		//Hud
		modules.add(new ArrayListE());
		modules.add(new Coords());
		modules.add(new DirectMsgs());
		modules.add(new Inventory());
		modules.add(new Watermark());
		//Misc
		modules.add(new ChestSearchBar());
		modules.add(new ExtraScreenshot());
		modules.add(new ExtraTab());
		modules.add(new FakePlayer());
		modules.add(new FastXp());
		modules.add(new FeetXp());
		modules.add(new MiddleClickPearl());
		modules.add(new PortalChat());
		modules.add(new StashLogger());
		//modules.add(new ToggleMsg());
		modules.add(new VisualRange());
		modules.add(new YawLock());
		//Movement
		modules.add(new Anchor());
		modules.add(new AutoJump());
		modules.add(new AutoWalk());
		modules.add(new NoSlow());
		modules.add(new OldfagNoFall());
		modules.add(new ReverseStep());
		modules.add(new SlowWalk());
		modules.add(new Sprint());
		modules.add(new Step());
		modules.add(new Velocity());
		//Render
		modules.add(new ClickGUI());
		modules.add(new Crosshair());
		modules.add(new Esp());
		modules.add(new FullBright());
		modules.add(new Nametags());
		modules.add(new NightMode());
		modules.add(new NoWeather());
		//modules.add(new Search());
		modules.add(new ShulkerPreview());
		modules.add(new SkyColor());
		modules.add(new Tracers());
	}
	
	public ArrayList<Module> getModules()
	{
		return modules;
	}

	public Module getModule(String name)
	{
		for (Module module : modules)
		{
			if (module.getName().equalsIgnoreCase(name)) return module;
		}

		return null;
	}

	public ArrayList<Module> getModules(Category category)
	{
		ArrayList<Module> mods = new ArrayList<>();

		for (Module module : modules)
		{
			if (module.getCategory().equals(category)) mods.add(module);
		}

		return mods;
	}

	public ArrayList<Module> getEnabledModules()
	{
		return modules.stream().filter(Module::isEnabled).collect(Collectors.toCollection(ArrayList::new));
	}
}
