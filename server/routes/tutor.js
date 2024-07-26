const express = require("express");
const Tutor = require("../models/tutor");
const Account = require("../models/account");
const auth = require("../middlewares/auth");
const Subject = require("../models/subject");

const tutorRouter = express.Router();

tutorRouter.post("/api/tutor", auth, async (req, res) => {
  try {
    const account = await Account.findById(req.account);
    const {
      firstName,
      lastName,
      dateOfBirth,
      gender,
      contactNumber,
      email,
      portraitPhotos,
      address,
      qualification,
      experience,
      graduationYear,
      teachingTime,
      citizenId,
      subjectIds,
    } = req.body;

    const subjects = await Subject.find({ _id: { $in: subjectIds } });
    if (subjects.length !== subjectIds.length) {
      return res.status(400).json({ message: 'Some subjects were not found' });
    }

    if (account.tutorId) {
      let tutor = await Tutor.findById(account.tutorId);
      tutor.firstName = firstName;
      tutor.lastName = lastName;
      tutor.dateOfBirth = dateOfBirth;
      tutor.gender = gender;
      tutor.contactNumber = contactNumber;
      tutor.email = email;
      tutor.portraitPhotos = portraitPhotos;
      tutor.address = address;
      tutor.qualification = qualification;
      tutor.experience = experience;
      tutor.graduationYear = graduationYear;
      tutor.teachingTime = teachingTime;
      tutor.citizenId = citizenId;
      tutor.subjects = subjectIds;

      await tutor.save();

      return res
        .status(200)
        .json({ message: "Tutor updated successfully", tutor, subjects });
    }

    const tutorData = new Tutor({
      firstName,
      lastName,
      dateOfBirth,
      gender,
      contactNumber,
      email,
      portraitPhotos,
      address,
      qualification,
      experience,
      graduationYear,
      teachingTime,
      citizenId,
      subjects: subjectIds,
    });

    await tutorData.save();

    const tutorId = tutorData._id;

    account.tutorId = tutorId;
    await account.save();

    res
      .status(201)
      .json({ message: "Tutor created successfully", tutor: tutorData, subjects });
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ error: "Failed to create / update tutor data" });
  }
});

tutorRouter.get('/api/tutor/get', auth, async (req, res) => {
    try {
        const account = await Account.findById(req.account);
        
        const tutor = await Tutor.findById(account.tutorId);
        if (!tutor)
            return res.status(404).json({ message: 'Tutor not found' });

        res.status(200).json({ tutor });
    } catch (error) {
        console.error(error.message);
        res.status(500).json({ error: 'Failed to get tutor data' });
    }
});

module.exports = tutorRouter;
