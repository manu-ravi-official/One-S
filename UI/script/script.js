const BASE_URL = "http://localhost:8080/";
let totalPages = 0;
function uploadFile() {
  const fileInput = document.getElementById("file");
  const statusText = document.getElementById("status");

  if (fileInput.files.length === 0) {
    alert("Please select a file first.");
    return;
  }

  const file = fileInput.files[0];
  const formData = new FormData();
  formData.append("file", file);

  fetch(`${BASE_URL}users`, {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      if (response.ok) {
        statusText.innerText = "Upload successful!";
        fetchData(0, 100, 0);
      } else {
        statusText.innerText = "Upload failed!";
      }
    })
    .catch((error) => {
      statusText.innerText = "Upload failed!";
      console.error("Error:", error);
    });
}

let currentPage = 0;
const pageSize = 10;

async function fetchData(minAge, maxAge, page) {
  try {
    const response = await fetch(
      `${BASE_URL}users?minAge=${minAge}&maxAge=${maxAge}&page=${page}&size=${pageSize}`
    );
    const data = await response.json();
    displayData(data);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

function displayData(data) {
  const tableBody = document.getElementById("table-body");
  tableBody.innerHTML = "";

  // Get export buttons and pagination controls
  const exportCSVBtn = document.getElementById("export-csv");
  const exportPDFBtn = document.getElementById("export-pdf");
  const prevBtn = document.getElementById("prev-btn");
  const nextBtn = document.getElementById("next-btn");
  const pageInfo = document.getElementById("page-info");
  const gotoPageInput = document.getElementById("goto-page");
  const goBtn = document.getElementById("go-btn");

  totalPages = data.totalPages;
  if (data.content.length === 0) {
    tableBody.innerHTML = `<tr><td colspan="4" style="text-align:center;">No data available</td></tr>`;
    exportCSVBtn.style.display = "none";
    exportPDFBtn.style.display = "none";
    pageInfo.style.display = "none";
    prevBtn.style.display = "none";
    nextBtn.style.display = "none";
    gotoPageInput.style.display = "none";
    goBtn.style.display = "none";
    return;
  }

  // Show buttons only if data is present
  exportCSVBtn.style.display = "inline-block";
  exportPDFBtn.style.display = "inline-block";
  pageInfo.style.display = "inline"; // Show page info if data is present
  prevBtn.style.display = data.first ? "none" : "inline-block";
  nextBtn.style.display = data.last ? "none" : "inline-block";
  gotoPageInput.style.display = totalPages > 1 ? "inline-block" : "none";
  goBtn.style.display = totalPages > 1 ? "inline-block" : "none";

  // Populate table
  data.content.forEach((user) => {
    const row = `<tr>
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>${user.age}</td>
        </tr>`;
    tableBody.innerHTML += row;
  });

  document.getElementById("page-info").innerText = `Page ${
    data.pageable.pageNumber + 1
  } of ${data.totalPages}`;
}
// Function to handle age range selection
function onAgeChange(resetPage = true) {
  const ageRange = document.getElementById("age-range").value.split("-");
  const minAge = parseInt(ageRange[0]);
  const maxAge = parseInt(ageRange[1]);

  if (resetPage) {
    currentPage = 0;
  }

  fetchData(minAge, maxAge, currentPage);
}

function changePage(step) {
  if (
    (step < 0 && currentPage === 0) ||
    (step > 0 && currentPage + 1 >= totalPages)
  )
    return;
  currentPage += step;
  onAgeChange(false);
}

document.addEventListener("DOMContentLoaded", () => {
  onAgeChange();
});
function goToPage() {
  const pageInput = document.getElementById("goto-page").value;
  const pageNumber = parseInt(pageInput, 10);

  if (isNaN(pageNumber) || pageNumber < 1 || pageNumber > totalPages) {
    alert(`Please enter a valid page number between 1 and ${totalPages}`);
    return;
  }

  currentPage = pageNumber - 1;

  onAgeChange(false);
}
async function exportCSV() {
  try {
    const [minAge, maxAge] = document
      .getElementById("age-range")
      ?.value?.split("-")
      ?.map(Number) || [0, 100]; // Default to 0-100 if undefined

    // Fetch CSV file
    const response = await fetch(
      `${BASE_URL}users/csv?minAge=${minAge}&maxAge=${maxAge}`,
      {
        method: "GET",
        headers: {
          Accept: "text/csv",
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Failed to download CSV: ${response.status}`);
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = `users_${minAge}-${maxAge}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Error exporting CSV:", error);
    alert("Failed to export CSV. Please try again.");
  }
}

async function exportPDF() {
  try {
    const [minAge, maxAge] = document
      .getElementById("age-range")
      ?.value?.split("-")
      ?.map(Number) || [0, 100]; // Default to 0-100 if undefined

    const response = await fetch(
      `${BASE_URL}users/pdf?minAge=${minAge}&maxAge=${maxAge}`,
      {
        method: "GET",
        headers: {
          Accept: "application/pdf",
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Failed to download PDF: ${response.status}`);
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = `users_${minAge}-${maxAge}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Error exporting PDF:", error);
    alert("Failed to export PDF. Please try again.");
  }
}
