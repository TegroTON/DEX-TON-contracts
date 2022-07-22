export function RemoveLiquidityModal() {
    return (
        <div className="modal fade" id="RemoveLiquidity" tabIndex={-1} aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered mobile-modal-bottom">
                <div className="modal-content border-0 rounded p-40">
                    <div className="modal-body text-center p-0">
                        <p className="fs-24 mb-40 pb-3">Are you sure you want to delete <span
                            className="d-inline d-md-block">the liquidation?</span></p>
                        <button type="button" className="btn btn-sm color-red me-3"
                                data-bs-dismiss="modal" aria-label="Close">Cancel
                        </button>
                        <button type="button" className="btn btn-sm btn-danger"><i
                            className="fa-regular fa-trash-can me-2"></i>Remove Liquidity
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}
