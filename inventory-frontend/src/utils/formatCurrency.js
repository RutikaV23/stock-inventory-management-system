const formatCurrency = (amount) => {
  if (amount == null || Number.isNaN(Number(amount))) return '₹0.00';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
  }).format(Number(amount));
};

export default formatCurrency;
