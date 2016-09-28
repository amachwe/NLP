package rd.ml.nlp.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import rd.ml.nlp.service.DigestAndWriteService;

@Command(name="digestandwrite", scope="nlp", description="Digest and Write to Database")
@Service
public class DigestAndWriteCommand implements Action {

	@Argument(index=0,name="directory",required=true,description="Root directory for digesting (recursive).")
	String directory=null;
	
	@Reference
	DigestAndWriteService service;
	
	@Override
	public Object execute() throws Exception {
		service.digestAndWrite(directory);
		
		return "Complete";
	}

	
}
