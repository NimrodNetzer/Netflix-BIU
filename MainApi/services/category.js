const mongoose = require('mongoose');
const Category = require('../models/category');
const Movie = require('../models/movie'); // Import the Movie model



// Create a new category
const createCategory = async (name, promoted) => {
  const existingCategory = await Category.findOne({ name });
  if (existingCategory) {
    throw new Error(`Category with name '${name}' already exists.`);
  }

  try {
    const category = new Category({ name, promoted });
    return await category.save();
  } catch (error) {
    throw new Error(`Error creating category: ${error.message}`);
  }
};

// Get a category by ID
const getCategoryById = async (id) => {
  try {
    const category = await Category.findById(id);
    if (!category) {
      return null;
    }
    return category;
  } catch (error){
    return null;
  }
}

// Get all categories with optional filters
const getCategories = async (filters = {}) => {
  try {
    return await Category.find(filters);
  } catch (error) {
    throw new Error(`Error fetching categories: ${error.message}`);
  }
};

// Update a category by ID
const updateCategory = async (id, name, promoted) => {
  const category = await getCategoryById(id);
  if (!category) {
    return null;
  }

  try {
    // ✅ Check if the new name already exists (excluding the current category)
    const existingCategory = await Category.findOne({ name });
    if (existingCategory && existingCategory._id.toString() !== id) {
      throw new Error(`A category with the name "${name}" already exists.`);
    }

    // ✅ Update fields
    if (name) category.name = name;
    if (typeof promoted !== 'undefined') category.promoted = promoted;

    await category.save();
    return category;
  } catch (error) {
    throw new Error(`Error updating category: ${error.message}`);
  }
};


// Delete a category by ID
const deleteCategory = async (id) => {
  const category = await getCategoryById(id);

  if (!category) {
    return null; // Category does not exist
  }

  try {
    // Check if any movie has this category as its only category
    const movieWithOnlyThisCategory = await Movie.findOne({ categories: { $eq: [id] } });

    if (movieWithOnlyThisCategory) {
      const error = new Error(
        `Cannot delete category "${category.name}" because it is the only category for the movie "${movieWithOnlyThisCategory.name}".`
      );
      error.status = 400; // Indicate this is a client-side error
      throw error;
    }

    // Remove the category from all movies that reference it
    await Movie.updateMany({ categories: id }, { $pull: { categories: id } });

    // Proceed with deletion of the category
    await category.deleteOne();
    return category;
  } catch (error) {
    if (!error.status) error.status = 500; // Default to server error if no status is provided
    throw error;
  }
};
// Retrieve all categories by ID (if filtering by specific IDs is required)
const getAllCategorieById = async (id) => {
  try {
    return await Category.find({ _id: id });
  } catch (error) {
    throw new Error(`Error fetching categories: ${error.message}`);
  }
};

module.exports = {
  getCategories,
  getCategoryById,
  createCategory,
  updateCategory,
  deleteCategory,
  getAllCategorieById,
};
