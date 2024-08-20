const express = require("express");
const mongoose = require("mongoose");
const Tutor = require("../models/tutor");
const Account = require("../models/account");
const auth = require("../middlewares/auth");
const Subject = require("../models/subject");
const moment = require('moment');

const tutorRouter = express.Router();

// tutorRouter.post("/api/tutor", async (req, res) => {
//   try {
//     const account = await Account.findById(req.account);
//     const {
//       firstName,
//       lastName,
//       dateOfBirth,
//       gender,
//       contactNumber,
//       email,
//       portraitPhotos,
//       address,
//       qualification,
//       experience,
//       graduationYear,
//       teachingTime,
//       citizenId,
//       subjectIds,
//     } = req.body;

//     // const subjects = await Subject.find({ _id: { $in: subjectIds } });
//     // if (!subjectIds) {
//     //   if (subjects.length !== subjectIds.length) {
//     //     return res.status(400).json({ message: 'Some subjects were not found' });
//     //   }
//     // }

//     if (account.tutorId) {
//       let tutor = await Tutor.findById(account.tutorId);
//       tutor.firstName = firstName;
//       tutor.lastName = lastName;
//       tutor.dateOfBirth = dateOfBirth;
//       tutor.gender = gender;
//       tutor.contactNumber = contactNumber;
//       tutor.email = email;
//       tutor.portraitPhotos = portraitPhotos;
//       tutor.address = address;
//       tutor.qualification = qualification;
//       tutor.experience = experience;
//       tutor.graduationYear = graduationYear;
//       tutor.teachingTime = teachingTime;
//       tutor.citizenId = citizenId;
//       tutor.subjects = subjectIds;

//       await tutor.save();

//       return res
//         .status(200)
//         .json({ message: "Tutor updated successfully", tutor, subjects });
//     }

//     const tutorData = new Tutor({
//       firstName,
//       lastName,
//       dateOfBirth,
//       gender,
//       contactNumber,
//       email,
//       portraitPhotos,
//       address,
//       qualification,
//       experience,
//       graduationYear,
//       teachingTime,
//       citizenId,
//       subjects: subjectIds,
//     });

//     await tutorData.save();

//     const tutorId = tutorData._id;

//     account.tutorId = tutorId;
//     await account.save();

//     res
//       .status(201)
//       .json({ message: "Tutor created successfully", tutor: tutorData, subjects });
//   } catch (error) {
//     console.error(error.message);
//     res.status(500).json({ error: "Failed to create / update tutor data" });
//   }
// });

tutorRouter.post("/api/tutor", async (req, res) => {
  try {
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
      subjectIds = [],
    } = req.body;

    let subjects = [];
    if (subjectIds.length > 0) {
      subjects = await Subject.find({ _id: { $in: subjectIds } });
      if (subjects.length !== subjectIds.length) {
        return res
          .status(400)
          .json({ message: "Some subjects were not found" });
      }
    }

    // Tạo mới tutor
    const newTutor = new Tutor({
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

    // Lưu tutor vào database
    await newTutor.save();

    // Trả về phản hồi
    res.status(201).json({
      message: "Tutor created successfully",
      tutor: newTutor,
      subjects,
    });
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ error: "Failed to create tutor" });
  }
});

tutorRouter.put("/api/tutors/:id", async (req, res) => {
  try {
    const tutorId = req.params.id;
    const updateData = req.body;

    if (updateData.dateOfBirth) {
      updateData.dateOfBirth = moment(updateData.dateOfBirth, "DD/MM/YYYY").toDate();
    }

    const updatedTutor = await Tutor.findByIdAndUpdate(tutorId, updateData, {
      new: true,
    }).populate("subjects");

    if (!updatedTutor) {
      return res.status(404).json({ message: "Tutor not found" });
    }

    const tutorData = {
      _id: updatedTutor._id,
      firstName: updatedTutor.firstName,
      lastName: updatedTutor.lastName,
      dateOfBirth: updatedTutor.dateOfBirth,
      gender: updatedTutor.gender,
      contactNumber: updatedTutor.contactNumber,
      email: updatedTutor.email,
      portraitPhotos: updatedTutor.portraitPhotos,
      address: updatedTutor.address,
      qualification: updatedTutor.qualification,
      experience: updatedTutor.experience,
      graduationYear: updatedTutor.graduationYear,
      teachingTime: updatedTutor.teachingTime,
      citizenId: updatedTutor.citizenId,
      subjects: updatedTutor.subjects.map((subject) => ({
        _id: subject._id,
        subjectName: subject.subjectName,
        grade: subject.grade,
      })),
      subjectIds: updatedTutor.subjects.map((subject) =>
        subject._id.toString()
      ),
      status: updatedTutor.status,
      registerDate: updatedTutor.registerDate,
    };

    res.status(200).json(tutorData);
  } catch (error) {
    console.error("Error updating tutor data:", error);
    res.status(500).json({ message: error.message });
  }
});

tutorRouter.get("/api/tutor/get", auth, async (req, res) => {
  try {
    const account = await Account.findById(req.account);

    if (!account) {
      return res.status(404).json({ message: "Account not found" });
    }

    const tutorId = new mongoose.Types.ObjectId(account.tutorId);

    const tutor = await Tutor.findOne({ _id: tutorId }).populate("subjects");

    if (!tutor) {
      return res.status(404).json({ message: "Tutor not found" });
    }

    res.status(200).json(tutor);
  } catch (error) {
    console.error("Error fetching tutor data:", error);
    res.status(500).json({ error: "Failed to get tutor data" });
  }
});

tutorRouter.get("/api/tutors", auth, async (req, res) => {
  try {
    const tutors = await Tutor.find().populate("subjects");

    if (tutors.length === 0) {
      return res.status(404).json({ message: "No tutors found" });
    }

    res.status(200).json(tutors);
  } catch (error) {
    console.error("Error fetching tutors data:", error);
    res.status(500).json({ error: "Failed to get tutors data" });
  }
});

tutorRouter.get("/api/get-tutor", auth, async (req, res) => {
  try {
    const tutor = await Tutor.findById(req.query.tutorId).populate("subjects");
    if (!tutor) {
      return res.status(404).json({ message: "Tutor not found" });
    }
    res.status(200).json(tutor);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

tutorRouter.get("/api/search-tutor", auth, async (req, res) => {
  try {
    const { q } = req.query;
    const regex = new RegExp(q, "i");
    const tutors = await Tutor.find({
      $or: [{ firstName: { $regex: regex } }, { lastName: { $regex: regex } }],
    }).populate("subjects");
    res.status(200).json(tutors);
  } catch (error) {
    res.status(500).json({ message: "Internal server error" });
  }
});

tutorRouter.get("/api/search-tutor-contact", auth, async (req, res) => {
  try {
    const { q } = req.query;
    if (!q || q.trim() === "") {
      return res.status(400).json({ message: "Search query is required" });
    }
    const regex = new RegExp(q, "i");
    const tutors = await Tutor.find({
      contactNumber: { $regex: regex },
    }).populate("subjects");
    res.status(200).json(tutors);
  } catch (error) {
    res.status(500).json({ message: "Internal server error" });
  }
});

tutorRouter.get("/api/tutor-id", async (req, res) => {
  const { contactNumber } = req.query;

  try {
    const tutor = await Tutor.findOne({ contactNumber: contactNumber }).select(
      "_id"
    );
    if (!tutor) {
      return res.status(404).send({ message: "Tutor not found" });
    }
    res.send(tutor._id);
  } catch (error) {
    res.status(500).send({ message: "Server error", error: error.message });
  }
});

module.exports = tutorRouter;
