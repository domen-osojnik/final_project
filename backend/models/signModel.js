var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var signScheme = new Schema({
	'naziv' : String,
	'tip_znakaID' : { type: Schema.Types.ObjectId, ref: 'sign_type' },
	'lokacijaID' : { type: Schema.Types.ObjectId, ref: 'location' }
});

module.exports = mongoose.model('sign', signScheme); //ZNAK
