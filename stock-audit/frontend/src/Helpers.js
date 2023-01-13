const addHours = (momentObj, hours) => {
  const dateObj = new Date(momentObj);
  dateObj.setHours(dateObj.getHours() + hours);

  return dateObj;
};

export const urlCreator = (formCtx, defaultUrl) => {
  let url = defaultUrl;

  if (formCtx.startDate !== null) {
    url += `?startDate=${addHours(formCtx.startDate, 2).toISOString()}`;
  }

  if (formCtx.endDate !== null) {
    if (url === defaultUrl) {
      url += `?endDate=${addHours(formCtx.endDate, 2).toISOString()}`;
    } else {
      url += `&endDate=${addHours(formCtx.endDate, 2).toISOString()}`;
    }
  }

  if (formCtx.companySymbol !== "") {
    if (url === defaultUrl) {
      url += `?symbol=${formCtx.companySymbol}`;
    } else {
      url += `&symbol=${formCtx.companySymbol}`;
    }
  }

  if (formCtx.companyName !== "") {
    if (url === defaultUrl) {
      url += `?company=${formCtx.companyName}`;
    } else {
      url += `&company=${formCtx.companyName}`;
    }
  }

  if (formCtx.searchField !== "") {
    if (url === defaultUrl) {
      url += `?field=${formCtx.searchField}`;
    } else {
      url += `&field=${formCtx.searchField}`;
    }
  }

  if (formCtx.valueOfField !== "") {
    if (url === defaultUrl) {
      url += `?word=${formCtx.valueOfField}`;
    } else {
      url += `&word=${formCtx.valueOfField}`;
    }
  }

  console.log(url);

  return url;
};
