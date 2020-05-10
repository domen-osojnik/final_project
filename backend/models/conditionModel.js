var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var conditionModel = new Schema({
    'naziv' : String
});

module.exports = mongoose.model('condition', conditionModel); //STANJE
