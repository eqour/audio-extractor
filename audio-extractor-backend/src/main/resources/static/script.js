const userId = 'c794f36a-a663-42f0-ada6-dd09676e8e9d'
const statuses = new Map()
const statusItems = new Map()

async function uploadFile(file, userId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    const response = await fetch('/upload', {
        method: 'POST',
        body: formData
    })
    if (!response.ok) {
        throw new Error('upload error')
    }
    return response.json()
}

async function getConversionStatus(fileId) {
    const response = await fetch(`/statuses/${fileId}`, {
        method: 'GET'
    })
    if (!response.ok) {
        throw new Error('fetch error')
    }
    return response.json()
}

function addStatusItem(root, status) {
    statuses.set(status.fileId, status)
    let itemWrapper = document.createElement('div')
    const content = generateStatusItemContent(status)
    content.forEach(value => itemWrapper.append(value))
    statusItems.set(status.fileId, itemWrapper)
    root.appendChild(itemWrapper)
}

function generateStatusItemContent(status) {
    const content = []
    let fileSpan = document.createElement('span')
    fileSpan.innerText = 'Файл'
    content.push(fileSpan)
    content.push(' ')
    let fileNameSpan = document.createElement('span')
    fileNameSpan.innerText = status.fileName
    content.push(fileNameSpan)
    content.push(' ')
    let fileStatusSpan = document.createElement('span')
    fileStatusSpan.innerText = status.stage
    content.push(fileStatusSpan)
    if (status.stage === 'PROCESSING') {
        content.push(' ')
        let filePercentSpan = document.createElement('span')
        filePercentSpan.innerText = `${status.progress}%`
        content.push(filePercentSpan)
    }
    if (status.stage === 'COMPLETED') {
        content.push(' ')
        let downloadLink = document.createElement('a')
        downloadLink.href = `/download/${status.fileId}`
        downloadLink.innerText = 'скачать'
        content.push(downloadLink)
    }
    return content
}

async function updateStatuses() {
    const statusKeysToUpdate = new Set()
    statuses.keys().forEach(key => {
        const status = statuses.get(key)
        if (status.stage !== 'COMPLETED') {
            statusKeysToUpdate.add(key)
        }
    })
    for (let fileId of statusKeysToUpdate) {
        try {
            const newStatus = await getConversionStatus(fileId)
            const statusData = {
                fileId: newStatus.fileId,
                fileName: newStatus.fileName,
                stage: newStatus.stage
            }
            if (newStatus.progress != null) statusData.progress = newStatus.progress
            statuses.set(fileId, statusData)
            const itemWrapper = statusItems.get(fileId)
            itemWrapper.replaceChildren()
            const content = generateStatusItemContent(statusData)
            content.forEach(value => itemWrapper.append(value))
        } catch (e) {
            console.error(e)
        }
    }
}

const fileInput = document.getElementById('upload-file-input')
const uploadButton = document.getElementById('upload-button')
const statusItemsContainer = document.getElementById("statuses")

uploadButton.addEventListener('click', async ignore => {
    try {
        const data = await uploadFile(fileInput.files[0])
        const status = await getConversionStatus(data.id)
        const statusData = {
            fileId: status.fileId,
            fileName: status.fileName,
            stage: status.stage
        }
        if (status.progress != null) statusData.progress = status.progress
        addStatusItem(statusItemsContainer, statusData)
    } catch (e) {
        console.error(e)
    }
})

setInterval(updateStatuses, 1000)
