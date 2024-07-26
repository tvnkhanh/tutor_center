const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const tutorSchema = new Schema({
    firstName: {
        type: String,
        required: true,
        trim: true,
    },
    lastName: {
        type: String,
        required: true,
        trim: true,
    },
    dateOfBirth: {
        type: Date,
        required: true,
    },
    gender: {
        type: String,
        required: true,
        enum: ['Male', 'Female', 'Other'],
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
        required: true,
        unique: true,
        validate: {
            validator: (value) => {
                const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\\.,;:\s@\"]+\.)+[^<>()[\]\\.,;:\s@\"]{2,})$/i;
                return re.test(value);
            },
            message: 'Please enter a valid email address'
        },
    },
    portraitPhotos: [
        {
            type: String,
            required: true,
        }
    ],
    address: {
        type: String,
        required: true,
    },
    qualification: [
        {
            type: String,
            required: true,
        }
    ],
    experience: {
        type: String,
        required: true,
    },
    graduationYear: {
        type: Number,
        required: true,
    },
    teachingTime: {
        type: String,
        required: true,
    },
    citizenId: {
        type: String,
        required: true,
        unique: true,
        validate: {
            validator: (value) => {
                const re = /^\d{9}$|^\d{12}$/; 
                return re.test(value);
            },
            message: 'Please enter a valid citizen ID'
        },
    },
    subjects: [
        {
            type: Schema.Types.ObjectId,
            ref: 'Subject'
        }
    ]
});

const Tutor = mongoose.model('Tutor', tutorSchema);
module.exports = Tutor;
