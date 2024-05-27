package pipeline;

import java.util.List;
import java.util.Map;

public class SoftwareTools {
	private Map<String, Object> runConfigurations;
	private Object software;
	
	public SoftwareTools(Map<String, Object> _runConfigurations) {
		this.runConfigurations = _runConfigurations;
		this.software = runConfigurations.get("class");
	}
	
//	public List<String> getBaseCommands() {
//		Object baseCommandsObj = runConfigurations.get("baseCommands");
//		
//		if (baseCommandsObj instanceof List) {
//			List<?> baseCommandList = (List<?>) baseCommandsObj;
//			if (baseCommandList.get(0) instanceof String) {
//				@SuppressWarnings("unchecked")
//				List<String> baseCommands = (List<String>) baseCommandList;
//				return baseCommands;
//			}
//		}
//		throw new IllegalArgumentException("No basecommands found for software: " + software);
//	}
	
	/**
	 * Returns and checks type of the specified values of baseCommand and flags from runConfigurations. 
	 * @param type the key of the value to return
	 * @return value of key type.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getCommands(String type) {
		Object typeObj = runConfigurations.get(type);
		
		if (typeObj instanceof List) {
			List<?> typeList = (List<?>) typeObj;
			for (Object obj : typeList) {
				if (!(obj instanceof String)) {
					throw new IllegalArgumentException("Flags are of different type for: " + software);
				}
			}
			return (List<String>) typeList;
			
		}
		throw new IllegalArgumentException("No flags found for software: " + software);
	}

}
