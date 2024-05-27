package pipeline;

public class RunGrapetree {
	
	private static String software = "grapetree";
	
	public RunGrapetree() {
		
	}
	
	public static void run() {
		
		RunDocker runGrapeTreeDocker = new RunDocker(software);
		runGrapeTreeDocker.runContainer();
		
	}
	
}
	
	


