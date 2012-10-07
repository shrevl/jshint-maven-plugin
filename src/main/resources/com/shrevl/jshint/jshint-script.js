this.options = this.options || {};
this.globals = this.globals || {};

var result = JSHINT(source, this.options, this.globals);

if(!result) {
	for (var i = 0, err; err = JSHINT.errors[i]; i++) {

		this.errors.push({
			reason: err.reason, 
			line: err.line, 
			character: err.character,
			evidence: err.evidence || ""
		});
	}
}