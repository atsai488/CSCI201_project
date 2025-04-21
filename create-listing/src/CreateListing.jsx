import 'bootstrap/dist/css/bootstrap.min.css';
import CustomFileInput from './CustomFileInput';

export default function CreateListing() {

    function handleSubmit(e){
        e.preventDefault()
        if (!e.target.checkValidity()){
            e.stopPropagation()
        }
        e.target.classList.add("was-validated")
    }
    return (
        <div className='bg-body-secondary pb-3'>
            <nav className="navbar">
                <div className="container-md">
                    <span className="navbar-brand my-1 h1 fs-2"> Create Listing </span>
                </div>
            </nav>
            <div className="container bg-light pt-4 px-4 rounded-4">
                <form 
                    action="confirmation" 
                    method="POST" 
                    encType='multipart/form-data' 
                    className='needs-validation'
                    onSubmit={handleSubmit} 
                    noValidate
                >
                    <div className="mb-3 col-lg-3 col-md-6 col-sm-9">
                        <label htmlFor="name" className="form-label fs-4"> Product Name</label>
                        <input 
                            type="text" 
                            className="form-control rounded-3" 
                            id="name" 
                            name="productName"
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
                    <div className="col-9">
                        <label htmlFor="images" className='form-label fs-4'> Add at least 3 pictures </label>
                        <div className="row row-cols-lg-3 row-cols-md-2 row-cols-1">
                            <CustomFileInput />
                            <CustomFileInput />
                            <CustomFileInput />
                        </div>
                    </div>
                    <div className="mb-3 col-lg-8 col-md-10 col-sm-12">
                        <label 
                            htmlFor="description" 
                            className="form-label fs-4"
                        > 
                             Product Description
                        </label>
                        <textarea 
                            className="form-control rounded-4" 
                            id="description" 
                            name='description' 
                            placeholder="Tell us about your product..." 
                            rows="6" 
                            required
                        ></textarea>
                        <div className="invalid-feedback">
                            Please enter a description of your product.
                        </div>
                        <div className="valid-feedback">
                            Looks good!
                        </div>
                    </div>
                    <div className='mb-3 d-flex justify-content-center'>
                         <button type="submit" className="btn btn-secondary mb-3 fs-3 px-4"> POST </button>
                    </div>
                </form>
            </div>
        </div>
    )
}