/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2021 STMicroelectronics.
  * All rights reserved.</center></h2>
  *
  * This software component is licensed by ST under BSD 3-Clause license,
  * the "License"; You may not use this file except in compliance with the
  * License. You may obtain a copy of the License at:
  *                        opensource.org/licenses/BSD-3-Clause
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "usb_device.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include "usbd_cdc_if.h"
#include "MadgwickAHRS.h"
#include <math.h>
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
#define M_PI 3.14159265358979323846f

#define MAG_X_OFFSET 0.0038163901390564424
#define MAG_Y_OFFSET -0.000156781993419
#define MAG_Z_OFFSET -0.000718401601466586

#define MAG_X_SCALE 0.02209077243785739
#define MAG_Y_SCALE 0.023881870638060895
#define MAG_Z_SCALE 0.02027771367194973

#define GYR_X_BIAS 0.0
#define GYR_Y_BIAS 0.0
#define GYR_Z_BIAS 0.0
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
I2C_HandleTypeDef hi2c1;

SPI_HandleTypeDef hspi1;

/* USER CODE BEGIN PV */
volatile uint8_t isr_flag = 1;
volatile uint8_t isr_acc = 1;
volatile uint8_t isr_gyr = 1;
volatile uint8_t isr_mag = 1;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_I2C1_Init(void);
static void MX_SPI1_Init(void);
/* USER CODE BEGIN PFP */

/* USER CODE BEGIN PFP */

uint8_t i2c1_pisiRegister(uint8_t, uint8_t, uint8_t);
void i2c1_beriRegistre(uint8_t, uint8_t, uint8_t*, uint8_t);
void initLSM303DLHC(void);

uint8_t spi1_beriRegister(uint8_t);
void spi1_beriRegistre(uint8_t, uint8_t*, uint8_t);
void spi1_pisiRegister(uint8_t, uint8_t);
void initL3GD20(void);

void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin);
/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */

// Pavza, s katero omogocimo pravilno delovanje avtomatskega testa
void pavza(){
  uint32_t counter = 0;
  for(counter=0; counter<600; counter++){
    asm("nop");
  }
}

////////////////////////////////////////////////////////////////////////// SPI1
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
uint8_t spi1_beriRegister(uint8_t reg)
{
  uint16_t buf_out, buf_in;
  reg |= 0x80; // najpomembnejsi bit na 1
  buf_out = reg; // little endian, se postavi na pravo mesto ....
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_RESET);
  pavza();
  //HAL_SPI_TransmitReceive(&hspi1, (uint8_t*)&buf_out, (uint8_t*)&buf_in, 2, 2); // blocking posiljanje ....
  HAL_SPI_TransmitReceive(&hspi1, &((uint8_t*)&buf_out)[0], &((uint8_t*)&buf_in)[0], 1, 2); // razbito na dva dela, da se podaljsa cas in omogoci pravilno delovanje testa
  pavza();
  HAL_SPI_TransmitReceive(&hspi1, &((uint8_t*)&buf_out)[1], &((uint8_t*)&buf_in)[1], 1, 2); // razbito na dva dela, da se podaljsa cas in omogoci pravilno delovanje testa
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_SET);
  pavza();
  return buf_in >> 8; // little endian...
}

void spi1_pisiRegister(uint8_t reg, uint8_t vrednost)
{
  uint16_t buf_out;
  buf_out = reg | (vrednost<<8); // little endian, se postavi na pravo mesto ....
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_RESET);
  pavza();
  //HAL_SPI_Transmit(&hspi1, (uint8_t*)&buf_out, 2, 2); // blocking posiljanje ....
  HAL_SPI_Transmit(&hspi1, &((uint8_t*)&buf_out)[0], 1, 2); // razbito na dva dela, da se podaljsa cas in omogoci pravilno delovanje testa
  pavza();
  HAL_SPI_Transmit(&hspi1, &((uint8_t*)&buf_out)[1], 1, 2); // razbito na dva dela, da se podaljsa cas in omogoci pravilno delovanje testa
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_SET);
  pavza();
}

void spi1_beriRegistre(uint8_t reg, uint8_t* buffer, uint8_t velikost)
{
  reg |= 0xC0; // najpomembnejsa bita na 1
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_RESET);
  pavza();
  HAL_SPI_Transmit(&hspi1, &reg, 1, 10); // blocking posiljanje....
  pavza();
  HAL_SPI_Receive(&hspi1,  buffer, velikost, velikost); // blocking posiljanje....
  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_3, GPIO_PIN_SET);
  pavza();
}

void initL3GD20()
{
  // preverimo ali smo "poklicali" pravi senzor
  uint8_t cip = spi1_beriRegister(0x0F);
  if (cip != 0xD4 && cip != 0xD3)
    for (;;);

  spi1_pisiRegister(0x20, 0x4F);// zbudi ziroskop in omogoci osi
  spi1_pisiRegister(0x22, 0x08);
  spi1_pisiRegister(0x23, 0x00);
}

/////////////////////////////////////////////////////////////////////////////////// I2C1
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
uint8_t i2c1_pisiRegister(uint8_t naprava, uint8_t reg, uint8_t podatek)
{
  naprava <<= 1;
  return HAL_I2C_Mem_Write(&hi2c1, naprava, reg, I2C_MEMADD_SIZE_8BIT, &podatek, 1, 10);
}

void i2c1_beriRegistre(uint8_t naprava, uint8_t reg, uint8_t* podatek, uint8_t dolzina)
{
  if ((dolzina>1)&&(naprava==0x19))  // ce je naprava 0x19 moramo postaviti ta bit, ce zelimo brati vec zlogov
    reg |= 0x80;
  naprava <<= 1;
  HAL_I2C_Mem_Read(&hi2c1, naprava, reg, I2C_MEMADD_SIZE_8BIT, podatek, dolzina, dolzina);
}

void initLSM303DLHC()
{
  HAL_Delay(10);

  // Za potrebe testa, moramo testni napravi sporociti kateri senzor imamo
  #define OLD_SENSOR 0x73 // Odkomentiramo za LSM303DLHC / stari senzor
  //#define NEW_SENSOR 0x6E // Odkomentiramo za LSM303AGR / novi senzor

  #if defined(OLD_SENSOR) && !defined(NEW_SENSOR)
  i2c1_pisiRegister(0x1e, 0x4F, OLD_SENSOR); // Povemo testni napravi, da imamo stari senzor
  #elif !defined(OLD_SENSOR) && defined(NEW_SENSOR)
  i2c1_pisiRegister(0x1e, 0x4F, NEW_SENSOR); // Povemo testni napravi, da imamo novi senzor
  #else
  for(;;); // V primeru napake, pocakamo tukaj
  #endif
  HAL_Delay(100);

  // inicializiraj pospeskometer
  i2c1_pisiRegister(0x19, 0x20, 0x57);  // zbudi pospeskometer in omogoci osi
  i2c1_pisiRegister(0x19, 0x22, 0x08);  // nastavi posodobitev samo ko se prebere vrednost ter visoko locljivost
  i2c1_pisiRegister(0x19, 0x23, 0x00);
}

void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin) {
	if(GPIO_Pin == GPIO_PIN_1) {
		isr_flag = 1;
		isr_gyr = 1;
	}
	if(GPIO_Pin == GPIO_PIN_2) {
		isr_flag = 1;
		isr_mag = 1;
	}
	if(GPIO_Pin == GPIO_PIN_4) {
		isr_flag = 1;
		isr_acc = 1;
	}
}

/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{
  /* USER CODE BEGIN 1 */

  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_I2C1_Init();
  MX_SPI1_Init();
  MX_USB_DEVICE_Init();
  /* USER CODE BEGIN 2 */
  __HAL_I2C_ENABLE(&hi2c1);
  initLSM303DLHC();

  int16_t meritev[4];
  meritev[0] = 0xaaab;// glava za zaznamek zacetek paketa
  uint8_t switch_mode = 0;
  uint8_t switch_usb = 0;

  uint16_t rawACC[3] = {0, 0, 0};
  uint16_t rawGYR[3] = {0, 0, 0};;
  uint16_t rawMAG[3] = {0, 0, 0};;

  uint16_t rawLatestIMU[10] = {0,0,0,0,0,0,0,0,0,0};
  rawLatestIMU[0] = 0xaaab;

  float boardOrientation[4] = {0.0,0.0,0.0,0.0};
  *(uint16_t *)&boardOrientation[0] = (uint16_t)0xaaab;

  float gyr_x = 0.0;
  float gyr_y = 0.0;
  float gyr_z = 0.0;

  float acc_x = 0.0;
  float acc_y = 0.0;
  float acc_z = 0.0;

  float mag_x = 0.0;
  float mag_y = 0.0;
  float mag_z = 0.0;
  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
	  if(isr_acc) {
		  i2c1_beriRegister(0x19, 0x28, (uint8_t*)&rawACC[0], 6);

		  rawLatestIMU[1] = rawACC[0];
		  rawLatestIMU[2] = rawACC[1];
		  rawLatestIMU[3] = rawACC[2];

		  isr_acc = 0;
	  }
	  if(isr_gyr) {
		  spi1_beriRegister(0x28, (uint8_t*)&rawGYR[0], 6);

		  rawLatestIMU[4] = rawGYR[0];
		  rawLatestIMU[5] = rawGYR[1];
		  rawLatestIMU[6] = rawGYR[2];

		  isr_gyr = 0;
	  }

	  if(isr_mag) {
		  i2c1_beriRegister(0x1E, 0x03, (uint8_t*)&rawMAG[0], 6);

		  rawLatestIMU[7] = rawMAG[0];
		  rawLatestIMU[8] = rawMAG[1];
		  rawLatestIMU[9] = rawmAG[2];

		  isr_mag = 0;
	  }

	  if(switch_usb) {
		  if(switch_mode) {
			  CDC_TRANSMIT_FS((uint8_t*)&rawLatestIMU, 20);
		  } else {
			  gyr_x = ((float)rawLatestIMU[4] / 32767.0 * 250.0 - GYR_X_BIAS) * M_PI / 180.0;
			  gyr_y = ((float)rawLatestIMU[5] / 32767.0 * 250.0 - GYR_X_BIAS) * M_PI / 180.0;
			  gyr_z = ((float)rawLatestIMU[6] / 32767.0 * 250.0 - GYR_X_BIAS) * M_PI / 180.0;

			  acc_x = (float)rawLatestIMU[1] / 32767.0 * 2.0;
			  acc_y = (float)rawLatestIMU[2] / 32767.0 * 2.0;
			  acc_z = (float)rawLatestIMU[3] / 32767.0 * 2.0;

			  mag_x = (((float)rawLatestIMU[7] / 32767.0 * 1.3) + MAG_X_OFFSET) / MAG_X_SCALE;
			  mag_y = (((float)rawLatestIMU[8] / 32767.0 * 1.3) + MAG_Y_OFFSET) / MAG_Y_SCALE;
			  mag_z = (((float)rawLatestIMU[9] / 32767.0 * 1.3) + MAG_Z_OFFSET) / MAG_Z_SCALE;

			  MadgwickAHRSupdate(-gyr_x, -gyr_y, gyr_z, acc_x, acc_y, acc_z, mag_x, mag_y, mag_z);

			  boardOrientation[1] = atan2(2*(q0*q1+q2*q3), 1-2*(q1*q1+q2*q2)) * 180/M_PI;
			  boardOrientation[2] = asin(2*(q0*q2-q3*q1)) * 180/M_PI;
			  boardOrientation[3] = atan2(2* (q0*q3+q1*q2), 1-2*(q2*q2+q3*q3)) * 180/M_PI;

			  CDC_Transmit_FS((uint8_t*)&boardOrientation, 16);
		  }

		  switch_usb = 0;
	  }
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};
  RCC_PeriphCLKInitTypeDef PeriphClkInit = {0};

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI|RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_BYPASS;
  RCC_OscInitStruct.HSEPredivValue = RCC_HSE_PREDIV_DIV1;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLMUL = RCC_PLL_MUL9;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }
  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV2;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_2) != HAL_OK)
  {
    Error_Handler();
  }
  PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_USB|RCC_PERIPHCLK_I2C1;
  PeriphClkInit.I2c1ClockSelection = RCC_I2C1CLKSOURCE_HSI;
  PeriphClkInit.USBClockSelection = RCC_USBCLKSOURCE_PLL_DIV1_5;
  if (HAL_RCCEx_PeriphCLKConfig(&PeriphClkInit) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief I2C1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_I2C1_Init(void)
{

  /* USER CODE BEGIN I2C1_Init 0 */

  /* USER CODE END I2C1_Init 0 */

  /* USER CODE BEGIN I2C1_Init 1 */

  /* USER CODE END I2C1_Init 1 */
  hi2c1.Instance = I2C1;
  hi2c1.Init.Timing = 0x0000020B;
  hi2c1.Init.OwnAddress1 = 0;
  hi2c1.Init.AddressingMode = I2C_ADDRESSINGMODE_7BIT;
  hi2c1.Init.DualAddressMode = I2C_DUALADDRESS_DISABLE;
  hi2c1.Init.OwnAddress2 = 0;
  hi2c1.Init.OwnAddress2Masks = I2C_OA2_NOMASK;
  hi2c1.Init.GeneralCallMode = I2C_GENERALCALL_DISABLE;
  hi2c1.Init.NoStretchMode = I2C_NOSTRETCH_DISABLE;
  if (HAL_I2C_Init(&hi2c1) != HAL_OK)
  {
    Error_Handler();
  }
  /** Configure Analogue filter
  */
  if (HAL_I2CEx_ConfigAnalogFilter(&hi2c1, I2C_ANALOGFILTER_ENABLE) != HAL_OK)
  {
    Error_Handler();
  }
  /** Configure Digital filter
  */
  if (HAL_I2CEx_ConfigDigitalFilter(&hi2c1, 0) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN I2C1_Init 2 */

  /* USER CODE END I2C1_Init 2 */

}

/**
  * @brief SPI1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_SPI1_Init(void)
{

  /* USER CODE BEGIN SPI1_Init 0 */

  /* USER CODE END SPI1_Init 0 */

  /* USER CODE BEGIN SPI1_Init 1 */

  /* USER CODE END SPI1_Init 1 */
  /* SPI1 parameter configuration*/
  hspi1.Instance = SPI1;
  hspi1.Init.Mode = SPI_MODE_MASTER;
  hspi1.Init.Direction = SPI_DIRECTION_2LINES;
  hspi1.Init.DataSize = SPI_DATASIZE_8BIT;
  hspi1.Init.CLKPolarity = SPI_POLARITY_HIGH;
  hspi1.Init.CLKPhase = SPI_PHASE_2EDGE;
  hspi1.Init.NSS = SPI_NSS_SOFT;
  hspi1.Init.BaudRatePrescaler = SPI_BAUDRATEPRESCALER_8;
  hspi1.Init.FirstBit = SPI_FIRSTBIT_MSB;
  hspi1.Init.TIMode = SPI_TIMODE_DISABLE;
  hspi1.Init.CRCCalculation = SPI_CRCCALCULATION_DISABLE;
  hspi1.Init.CRCPolynomial = 7;
  hspi1.Init.CRCLength = SPI_CRC_LENGTH_DATASIZE;
  hspi1.Init.NSSPMode = SPI_NSS_PULSE_DISABLE;
  if (HAL_SPI_Init(&hspi1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN SPI1_Init 2 */

  /* USER CODE END SPI1_Init 2 */

}

/**
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
  GPIO_InitTypeDef GPIO_InitStruct = {0};

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOE_CLK_ENABLE();
  __HAL_RCC_GPIOC_CLK_ENABLE();
  __HAL_RCC_GPIOF_CLK_ENABLE();
  __HAL_RCC_GPIOA_CLK_ENABLE();
  __HAL_RCC_GPIOB_CLK_ENABLE();

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOE, CS_I2C_SPI_Pin|LD4_Pin|LD3_Pin|LD5_Pin
                          |LD7_Pin|LD9_Pin|LD10_Pin|LD8_Pin
                          |LD6_Pin, GPIO_PIN_RESET);

  /*Configure GPIO pins : DRDY_Pin MEMS_INT3_Pin MEMS_INT4_Pin MEMS_INT1_Pin
                           MEMS_INT2_Pin */
  GPIO_InitStruct.Pin = DRDY_Pin|MEMS_INT3_Pin|MEMS_INT4_Pin|MEMS_INT1_Pin
                          |MEMS_INT2_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_EVT_RISING;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  HAL_GPIO_Init(GPIOE, &GPIO_InitStruct);

  /*Configure GPIO pins : CS_I2C_SPI_Pin LD4_Pin LD3_Pin LD5_Pin
                           LD7_Pin LD9_Pin LD10_Pin LD8_Pin
                           LD6_Pin */
  GPIO_InitStruct.Pin = CS_I2C_SPI_Pin|LD4_Pin|LD3_Pin|LD5_Pin
                          |LD7_Pin|LD9_Pin|LD10_Pin|LD8_Pin
                          |LD6_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(GPIOE, &GPIO_InitStruct);

  /*Configure GPIO pin : B1_Pin */
  GPIO_InitStruct.Pin = B1_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_INPUT;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  HAL_GPIO_Init(B1_GPIO_Port, &GPIO_InitStruct);

}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */

  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     tex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
