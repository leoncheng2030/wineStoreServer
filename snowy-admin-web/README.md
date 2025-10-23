# Snowy Admin Web

小诺团队旗下Snowy前端，基于Antdv4+Vue3+Vite

## 动态配置后端接口地址

项目支持在打包部署后动态配置后端接口地址，无需重新编译。

### 使用方法

1. 构建项目:
   ```bash
   npm run build
   ```

2. 部署dist目录下的文件到服务器

3. 修改`config.js`文件中的`VITE_API_BASEURL`配置项:
   ```javascript
   window.SNOWY_CONFIG = {
     // 后端接口地址
     VITE_API_BASEURL: 'https://your-api-address.com'
   }
   ```

注意：修改config.js后需要刷新页面才能生效。

## 开发

```bash
npm run dev
```

## 构建

```bash
npm run build
```