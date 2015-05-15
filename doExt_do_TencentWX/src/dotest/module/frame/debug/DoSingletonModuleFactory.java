package dotest.module.frame.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.interfaces.DoISingletonModuleFactory;
import core.interfaces.DoISingletonModuleGroup;
import core.interfaces.DoIScriptEngine;
import core.object.DoSingletonModule;

public class DoSingletonModuleFactory implements DoISingletonModuleFactory {

	private List<DoISingletonModuleGroup> listSingletonModuleGroup = new ArrayList<DoISingletonModuleGroup>();
	public Map<String, DoSingletonModule> dictSingletonModules = new HashMap<String, DoSingletonModule>();
	private Map<String, DoSingletonModule> dictSingletonModuleAddresses = new HashMap<String, DoSingletonModule>();

	@Override
	public DoSingletonModule getSingletonModuleByID(DoIScriptEngine _scriptEngine, String _typeID) throws Exception {
		return this.dictSingletonModules.get(_typeID);
	}

	@Override
	public DoSingletonModule getSingletonModuleAddress(String _key) {
		if (!this.dictSingletonModuleAddresses.containsKey(_key))
			return null;
		return this.dictSingletonModuleAddresses.get(_key);
	}

	@Override
	public void removeSingletonModuleByAddress(String _key) {
		if (this.dictSingletonModuleAddresses.containsKey(_key)) {
			this.dictSingletonModuleAddresses.remove(_key);
		}
	}

	@Override
	public void registGroup(DoISingletonModuleGroup _singletonModuleGroup) {
		this.listSingletonModuleGroup.add(_singletonModuleGroup);
	}

}
