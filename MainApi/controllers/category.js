const categoryService = require('../services/category');

const getCategories = async (req, res) => {
  try {
    const filters = {};
    if (req.query.promoted) {
      filters.promoted = req.query.promoted === 'true'; // Convert string to boolean
    }
    const categories = await categoryService.getCategories(filters);
    res.json(categories);
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
};

const createCategory = async (req, res) => {
  try {
    const { name, promoted } = req.body;
    if (!name) {
      return res.status(400).json({ error: 'Name is required' });
    }

    const category = await categoryService.createCategory(name, promoted);
    const categoryUrl = `/api/categories/${category._id}`;
    
    // Set Location header
    res.setHeader('Location', categoryUrl);
    res.status(201).json();
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

const getCategory = async (req, res) => {
  try {
    const { id } = req.params;
    const category = await categoryService.getCategoryById(id);
    
    if (!category) {
      return res.status(404).json({ error: 'Category not found' });
    }

    res.json(category);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

const updateCategory = async (req, res) => {
  try {
    const allowedFields = ['id','_id','name', 'promoted'];

    const bodyKeys = Object.keys(req.body);
    const hasInvalidField = bodyKeys.some((key) => !allowedFields.includes(key));

    if (hasInvalidField) {
      console.log('Invalid field');
      return res.status(400).json({
        error: 'Only "name" and "promoted" fields are allowed.',
      });
    }
    const { id } = req.params;
    const { name, promoted } = req.body;
    const category = await categoryService.updateCategory(id, name, promoted);
    if (!category) {
      return res.status(404).json({ error: 'Category not found' });
    }
    const categoryUrl = `/api/categories/${category._id}`;
    res.setHeader('Location', categoryUrl);
    res.status(204).json();
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

const deleteCategory = async (req, res) => {
  try {
    const { id } = req.params;
    const category = await categoryService.deleteCategory(id);
    if (!category) {
      return res.status(404).json({ error: 'Category not found' });
    }
    const categoryUrl = `/api/categories/${category._id}`;
    res.setHeader('Location', categoryUrl);
    res.status(204).send();
  } catch (error) {
    const statusCode = error.status || 500; // Use the status from the error or default to 500
    res.status(statusCode).json({ error: error.message });
  }
};


module.exports = {
  getCategories,
  createCategory,
  getCategory,
  updateCategory,
  deleteCategory,
};
