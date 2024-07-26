const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const clientSchema = new Schema({
    firstName: {
        type: String,
        required: true,
        trim: true
    },
    lastName: {
        type: String,
        required: true,
        trim: true
    },
    contactNumber: {
        type: String,
        required: true,
        unique: true,
        validate: {
            validator: (value) => {
                const re = /^(?:\+84|0)\d{9,10}$/;
                return re.test(value);
            },
            message: 'Please enter a valid contact number'
        },
    },
    email: {
        type: String,
        unique: true,
        validate: {
            validator: (value) => {
                const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\\.,;:\s@\"]+\.)+[^<>()[\]\\.,;:\s@\"]{2,})$/i;
                return re.test(value);
            },
            message: 'Please enter a valid email address'
        },
    },
});

const Client = mongoose.model('Client', clientSchema);
module.exports = Client;