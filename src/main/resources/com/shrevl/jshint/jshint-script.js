var options = {},
	globals = {};

var result = JSHINT(source, options, globals);

if(!result) {
	for (var i = 0, err; err = JSHINT.errors[i]; i++) {

		this.errors.push({
			file: this.currentFile, 
			reason: err.reason, 
			line: err.line, 
			character: err.character,
			evidence: err.evidence || ""
		});
	}
}