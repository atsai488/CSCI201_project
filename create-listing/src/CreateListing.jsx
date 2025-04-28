import 'bootstrap/dist/css/bootstrap.min.css';
import CustomFileInput from './CustomFileInput';
import './CreateListing.css'

export default function CreateListing() {

    function handleSubmit(e) {
        e.preventDefault()
        if (!e.target.checkValidity()) {
            e.stopPropagation()
        }
        e.target.classList.add("was-validated")
    }
    return (
        <div>
            <nav className="navbar navbar-expand-lg mb-3 pt-0">
                <div 
                    className="container-fluid px-4"
                    style={{
                        backgroundColor: "#990000",
                    }}
                >
                    <span 
                        className="navbar-brand fs-4"
                        style={{color: "#FFCC00"}}
                    >   
                        <strong>Add New Product </strong>
                    </span>
                </div>
            </nav>
            <div className="container ps-4 rounded-4">
                <div className='col-3 mb-3'>
                    <h2 className='fs-5 mb-2'style={{color: "#990000"}}>
                        <strong>Product Information</strong>
                    </h2>
                    <hr className='border-4 mt-1' style={{borderColor: "#FFCC00"}}/>
                </div>
                <form
                    action="confirmation"
                    method="POST"
                    encType='multipart/form-data'
                    className='needs-validation'
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <div className="mb-3 col-lg-7 col-md-8 col-sm-12">
                        <label htmlFor="name" className="form-label fs-6"> Product Name<span className='text-danger'>*</span></label>
                        <input
                            type="text"
                            className="form-control rounded-3"
                            id="name"
                            name="productName"
                            placeholder='Make it easy to find by including descriptive keywords'
                            autoComplete='off'
                            required
                        />
                        <div className="invalid-feedback">
                            Please provide a name for your product
                        </div>
                        <div className="valid-feedback">
                            Looks good!
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
                                    className='form-control'
                                    placeholder='0.00'
                                    pattern="^\d+(\.\d{1,2})?$"
                                    required
                                />
                                <div className="invalid-feedback">
                                    Please provide a valid price
                                </div>
                                <div className="valid-feedback">
                                    Looks good!
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
                                <div className="valid-feedback">
                                    Looks good!
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="mb-3 col-lg-8 col-md-10 col-sm-12">
                        <label htmlFor="description" className="form-label fs-6">
                            Product Description
                            <span className='text-danger'>*</span>
                        </label>
                        <textarea
                            className="form-control rounded-4"
                            id="description"
                            name='description'
                            placeholder="Tell us about your product..."
                            rows="4"
                            required
                        ></textarea>
                        <div className="invalid-feedback">
                            Please enter a description of your product.
                        </div>
                        <div className="valid-feedback">
                            Looks good!
                        </div>
                    </div>
                    <div className='col-3 mb-3'>
                        <h2 className='fs-5 mb-2' style={{color: "#990000"}}>
                            <strong>Product Images </strong>
                        </h2>
                        <hr className='border-4 mt-1' style={{borderColor: "#FFCC00"}} />
                    </div>
                    <div className="col-9 mb-3">
                        <div className="row gap-3">
                            <CustomFileInput />
                            <CustomFileInput />
                            <CustomFileInput />
                        </div>
                    </div>
                    <div className='d-flex justify-content-start'>
                        <button 
                            type="submit" 
                            className="btn mb-3 fs-6 py-2 px-4"
                            style={{
                                backgroundColor: "#990000",
                                color: "#FFCC00"
                            }}
                        ><strong> Create New Listing</strong></button>
                    </div>
                </form>
            </div>
        </div>
    )
}