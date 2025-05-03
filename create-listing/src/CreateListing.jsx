import 'bootstrap/dist/css/bootstrap.min.css';
import './CreateListing.css'
import { useState } from 'react';

export default function CreateListing() {

    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState(null)
    const [formData, setFormData] = useState({
        name: '',
        price: '',
        category: '',
        description: '',
        image1: null,
        image2: null,
        image3: null
    });

    function handleChange(e) {
        const { name, value } = e.target
        setFormData(prev => ({ ...prev, [name]: value }));
    }


    async function handleSubmit(e) {
        e.preventDefault()

        const form = e.target
        if (!form.checkValidity()) {
            e.stopPropagation()
            form.classList.add("was-validated")
            return
        }

        setIsLoading(true)
        setError(null)
        
        try {
            const response = await fetch("/create-listing-servlet", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    ...formData,
                    email: localStorage.getItem('userEmail')
                })
            });
            const results = await response.json()

            if (results.error) {
                setError(results.error)
            } else {
                window.location.href = "/"
            }
        } catch (e) {
            setError("Failed to create listing")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div>
            <nav className="navbar mb-3 pt-0">
                <div
                    className="container-fluid px-3 d-flex justify-content-between fw-bold"
                    style={{ backgroundColor: "#990000" }}
                >
                    <a
                        href='/'
                        className="navbar-brand fs-4"
                        style={{ color: "#FFCC00" }}
                    >
                        USC Marketplace
                    </a>
                    <span
                        className="navbar-brand me-0 fs-5"
                        style={{ color: "#FFCC00" }}
                    >
                        Add New Product
                    </span>
                </div>
            </nav>
            <div className="container ps-4 rounded-4">
                <div className='col-3 mb-3'>
                    <h2 className='fs-5 mb-2' style={{ color: "#990000" }}>
                        <strong>Product Information</strong>
                    </h2>
                    <hr className='border-4 mt-1' style={{ borderColor: "#FFCC00" }} />
                </div>
                <form
                    method="POST"
                    className='needs-validation'
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <div className="mb-3 col-lg-7 col-md-8 col-sm-12">
                        <label htmlFor="name" className="form-label fs-6">
                            Name
                            <span className='text-danger'>*</span>
                        </label>
                        <input
                            type="text"
                            className="form-control rounded-3"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder='Make it easy to find by including descriptive keywords'
                            autoComplete='off'
                            required
                        />
                        <div className="invalid-feedback">
                            Please provide a name for your product
                        </div>
                    </div>
                    <div className='col-10 d-flex gap-4'>
                        <div className='col-4'>
                            <div className="mb-3 col-lg-12 col-md-4 col-sm-8">
                                <label htmlFor="price" className='form-label fs-6'>
                                    Price
                                    <span className='text-danger'>*</span>
                                </label>
                                <input
                                    type="text"
                                    id='price'
                                    name='price'
                                    value={formData.price}
                                    onChange={handleChange}
                                    className='form-control'
                                    placeholder='0.00'
                                    pattern="^\d+(\.\d{1,2})?$"
                                    required
                                />
                                <div className="invalid-feedback">
                                    Please provide a valid price
                                </div>
                            </div>
                        </div>
                        <div className="col-4">
                            <div className="mb-3 col-lg-12">
                                <label htmlFor="category" className='form-label fs-6'>
                                    Category
                                    <span className='text-danger'>*</span>
                                </label>
                                <select
                                    className='form-select'
                                    name="category"
                                    id="category"
                                    value={formData.category}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="" disabled selected>Select a category</option>
                                    <option value="clothing">Clothing</option>
                                    <option value="electronics">Electronics</option>
                                    <option value="home-garden">Home & Garden</option>
                                    <option value="sports">Sports & Outdoors</option>
                                    <option value="beauty">Beauty & Personal Care</option>
                                    <option value="toys">Toys & Games</option>
                                    <option value="books">Books</option>
                                    <option value="automotive">Automotive</option>
                                    <option value="health">Health & Wellness</option>
                                    <option value="jewelry">Jewelry</option>
                                    <option value="art">Art & Collectibles</option>
                                    <option value="furniture">Furniture</option>
                                </select>
                                <div className="invalid-feedback">
                                    Please select a category
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="mb-3 col-lg-8 col-md-10 col-sm-12">
                        <label htmlFor="description" className="form-label fs-6">
                            Description
                            <span className='text-danger'>*</span>
                        </label>
                        <textarea
                            className="form-control rounded-4"
                            id="description"
                            name='description'
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Tell us about your product..."
                            rows="4"
                            required
                        ></textarea>
                        <div className="invalid-feedback">
                            Please enter a description of your product.
                        </div>
                    </div>
                    <div className='col-3 mb-3'>
                        <h2 className='fs-5 mb-2' style={{ color: "#990000" }}>
                            <strong>Product Images </strong>
                        </h2>
                        <hr className='border-4 mt-1' style={{ borderColor: "#FFCC00" }} />
                    </div>
                    <div className="col-9 mb-3">
                        <div className="row gap-3">
                            <input
                                type="text"
                                className="form-control rounded-3"
                                id="name"
                                name="image1"
                                value={formData.image1}
                                onChange={handleChange}
                                placeholder='Enter thumbnail photo URL'
                                autoComplete='off'
                                required
                            />
                            <input
                                type="text"
                                className="form-control rounded-3"
                                id="name"
                                name="image2"
                                value={formData.image2}
                                onChange={handleChange}
                                placeholder='Enter photo 2 URL'
                                autoComplete='off'
                                required
                            />
                            <input
                                type="text"
                                className="form-control rounded-3"
                                id="name"
                                name="image3"
                                value={formData.image3}
                                onChange={handleChange}
                                placeholder='Enter photo 3 URL'
                                autoComplete='off'
                                required
                            />
                            </div>
                    </div>
                    <div className='d-flex justify-content-start align-items-center'>
                        <button type="submit" id='custom-btn' className="btn mb-3 fs-6 py-2 px-4 fw-bold" disabled={isLoading}>
                            {isLoading ? "Creating..." : "Create New Listing"}
                        </button>
                        {error && (
                            <div className="ms-5 alert alert-danger">
                                {error}
                            </div>
                        )}
                    </div>
                </form>
            </div>
        </div>
    )
}