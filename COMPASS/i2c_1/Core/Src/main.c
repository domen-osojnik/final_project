/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2020 STMicroelectronics.
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

/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
I2C_HandleTypeDef hi2c1;

SPI_HandleTypeDef hspi1;

TIM_HandleTypeDef htim3;

/* USER CODE BEGIN PV */

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_I2C1_Init(void);
static void MX_SPI1_Init(void);
static void MX_TIM3_Init(void);
/* USER CODE BEGIN PFP */
void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim);
uint8_t i2c1_pisiRegister(uint8_t, uint8_t, uint8_t);
void i2c1_beriRegistre(uint8_t, uint8_t, uint8_t*, uint8_t);
void initLSM303DLHC(void);

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
char resBuff[4] = {"ABAA"};
int8_t dir = -1;

void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {

  // preveri ali je prekinitev klicana iz casovnika 3
  if (htim->Instance == TIM3) {
	  if (dir > -1) {
		  mapDirectionToChar();
		  CDC_Transmit_FS(resBuff, 4);
	  }
  }

}
/*** Using magnetometer calibration algorithm ***/

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

  // inicializiraj mag
  i2c1_pisiRegister(0x1E, 0x00, 0xC);  // 7.5hz
  i2c1_pisiRegister(0x1E, 0x01, 0x20);  // privzeto
  i2c1_pisiRegister(0x1E, 0x02, 0x00);

// inicializiraj pospeskometer
i2c1_pisiRegister(0x19, 0x20, 0x47);  // zbudi pospeskometer in omogoci osi IN nastavite odzivnost senzorja na 50Hz  Normal / low-power mode (50 Hz) 0 1 0 0
i2c1_pisiRegister(0x19, 0x23, 0x98);  // nastavi posodobitev samo ko se prebere vrednost ter visoko locljivost IN na +-4g (f1 f0 na 0 1)
}


void TurnOffPrevious(int PIN){
	switch (PIN)
	{
		case 8:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_8, GPIO_PIN_RESET);
		case 9:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_9, GPIO_PIN_RESET);
		case 10:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_10, GPIO_PIN_RESET);
		case 11:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_11, GPIO_PIN_RESET);
		case 12:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_12, GPIO_PIN_RESET);
		case 13:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_13, GPIO_PIN_RESET);
		case 14:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_14, GPIO_PIN_RESET);
		case 15:
			HAL_GPIO_WritePin(GPIOE, GPIO_PIN_15, GPIO_PIN_RESET);
	}
}

void setResBuf(char one, char two) {
	resBuff[2] = one;
	resBuff[3] = two;
}

void mapDirectionToChar() {
	switch (dir) {
	case 0:
		setResBuf('0','S');
		break;
	case 1:
		setResBuf('S','V');
		break;
	case 2:
		setResBuf('0','V');
		break;
	case 3:
		setResBuf('J','V');
		break;
	case 4:
		setResBuf('0','J');
		break;
	case 5:
		setResBuf('J','Z');
		break;
	case 6:
		setResBuf('0','Z');
		break;
	case 7:
		setResBuf('S','Z');
		break;
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
  MX_TIM3_Init();
  /* USER CODE BEGIN 2 */

  __HAL_I2C_ENABLE(&hi2c1);

  int16_t meritev[5];
  meritev[0] = 0xaaab;// glava za zaznamek zacetek paketa

  initLSM303DLHC();


  char toReturn[250] = {0};
  int toggleCount = 0;
  int16_t packetCount = 0; // stevec klikov
  _Bool isToggled = 0;

  int prevLight = 0;

  int8_t btnCtrl = 0;
  int8_t debug = 0;
  HAL_TIM_Base_Start_IT(&htim3);

  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {

	  // Debug opcija ON/OFF
	  // ON (0): send 4B float
	  // OFF(1): send JSON string

	  if (HAL_GPIO_ReadPin(GPIOA, GPIO_PIN_0) && debug == 0) {
		  debug = 1;
	  }
	  else if (!HAL_GPIO_ReadPin(GPIOA, GPIO_PIN_0) && debug == 1) {
		  debug = 0;
	  }

	  HAL_Delay(100);


	  meritev[1] = packetCount;
	  // Convert to ASCII
	  // Beri l in h registre

	  uint8_t x1, x2, y1,y2, z1,z2;

	  // Preberi x y z vrednosti po 8 bitov , high low
	  // X
	  i2c1_beriRegistre(0x1E,  0x03,(uint8_t*)&x1, 1);
	  i2c1_beriRegistre(0x1E,  0x04,(uint8_t*)&x2, 1);
	  // Z
	  i2c1_beriRegistre(0x1E,  0x05,(uint8_t*)&z1, 1);
	  i2c1_beriRegistre(0x1E,  0x06,(uint8_t*)&z2, 1);
	  // Y
	  i2c1_beriRegistre(0x1E,  0x07,(uint8_t*)&y1, 1);
	  i2c1_beriRegistre(0x1E,  0x08,(uint8_t*)&y2, 1);


	  uint16_t x,y,z;
	  // sestavi 16bitni int

	  meritev[2] = ((x1 << 8) | (x2 & 0xff));
	  meritev[3] = ((y1 << 8) | (y2 & 0xff));
	  meritev[4] = ((z1 << 8) | (z2 & 0xff));

	  float xv, yv, zv;

	  xv = (float)meritev[2] / 1100.0f;
	  yv = (float)meritev[3] / 1100.0f;
	  zv = (float)meritev[4] / 980.0f;

	  xv=(xv+0.076575923160515f)/0.425442085836195f;
	  yv=(yv+0.0278623169268702f)/0.45627238404055f;
	  zv=(zv+0.00343904513673798f)/0.447764326965475f;

	  // GENERATE BEARING
	  float Pi = 3.14159;

	  // Calculate the angle of the vector y,x -> also GAUS to microteslas
	  float heading = (atan2((yv/100.0f), (xv/100.0f)) * 180) / Pi;

	  // Normalize to 0-360
	  if (heading < 0) {
		heading = 360 + heading;
	  }

	  // ACC MERITVE
		  // Preberi x y z vrednosti
      i2c1_beriRegistre(0x19, 0x28,(uint16_t*)&meritev[2], 2);
      // Y
      i2c1_beriRegistre(0x19, 0x2A,(uint16_t*)&meritev[3], 2);
      // Z
      i2c1_beriRegistre(0x19, 0x2C,(uint16_t*)&meritev[4], 2);

      // https://ozzmaker.com/accelerometer-to-g/
      float range = 0.122f;
	  // Convert to ASCII

	  float xAcc, yAcc, zAcc;
	  xAcc = (float)(meritev[2] * range) / 1000.0f;
	  xAcc = (float)(meritev[3]* range) / 1000.0f;
	  zAcc = (float)(meritev[4]* range) / 1000.0f;


	  // S
	  if(heading <= 22.5f || heading >= 337.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 13;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_13, GPIO_PIN_SET);
			  prevLight = 13;
		  }

		  dir = 0;
      }
	  // SV
	  if(heading <= 67.5f && heading >= 22.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 14;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_14, GPIO_PIN_SET);
			  prevLight = 14;
		  }

		  dir = 1;
      }

	  // V
	  if(heading >= 67.5f && heading <= 112.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 15;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_15, GPIO_PIN_SET);
			  prevLight = 15;
		  }

		  dir = 2;
      }

	  // JV
	  if(heading >= 112.5f && heading <= 157.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 8;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_8, GPIO_PIN_SET);
			  prevLight = 8;
		  }

		  dir = 3;
      }

	  // J
	  if(heading >= 157.5f && heading <= 202.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 9;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_9, GPIO_PIN_SET);
			  prevLight = 9;
		  }

		  dir = 4;
      }

	  // JZ
	  if(heading >= 202.5f && heading <= 247.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 10;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_10, GPIO_PIN_SET);
			  prevLight = 10;
		  }

		  dir = 5;
      }

	  // Z
	  if(heading >= 247.5f && heading <= 292.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 11;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_11, GPIO_PIN_SET);
			  prevLight = 11;
		  }

		  dir = 6;
      }

	  // SZ
	  if(heading >= 292.5f && heading <= 337.5f)
      {
		  if(prevLight == 0)
		  {
			  prevLight = 12;
		  }

		  else
		  {
			  TurnOffPrevious(prevLight);
			  HAL_GPIO_WritePin(GPIOE, GPIO_PIN_12, GPIO_PIN_SET);
			  prevLight = 12;
		  }

		  dir = 7;
      }

		packetCount++;

		// DEBUG MODE
		if (debug == 1) {
			int strlen = snprintf(NULL, 0, "{Compass Heading: %.3f}\n\r", (float) heading) + 1;
			char *stringToSend = malloc(strlen);
			snprintf(stringToSend, strlen, "{Compass Heading: %.3f}\n\r", (float) heading);
			memset(toReturn, 0, sizeof(toReturn));
			CDC_Transmit_FS(stringToSend, strlen);
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
  RCC_OscInitStruct.PLL.PLLMUL = RCC_PLL_MUL6;
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

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_1) != HAL_OK)
  {
    Error_Handler();
  }
  PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_USB|RCC_PERIPHCLK_I2C1;
  PeriphClkInit.I2c1ClockSelection = RCC_I2C1CLKSOURCE_HSI;
  PeriphClkInit.USBClockSelection = RCC_USBCLKSOURCE_PLL;
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
  hi2c1.Init.Timing = 0x2000090E;
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
  hspi1.Init.DataSize = SPI_DATASIZE_4BIT;
  hspi1.Init.CLKPolarity = SPI_POLARITY_LOW;
  hspi1.Init.CLKPhase = SPI_PHASE_1EDGE;
  hspi1.Init.NSS = SPI_NSS_SOFT;
  hspi1.Init.BaudRatePrescaler = SPI_BAUDRATEPRESCALER_4;
  hspi1.Init.FirstBit = SPI_FIRSTBIT_MSB;
  hspi1.Init.TIMode = SPI_TIMODE_DISABLE;
  hspi1.Init.CRCCalculation = SPI_CRCCALCULATION_DISABLE;
  hspi1.Init.CRCPolynomial = 7;
  hspi1.Init.CRCLength = SPI_CRC_LENGTH_DATASIZE;
  hspi1.Init.NSSPMode = SPI_NSS_PULSE_ENABLE;
  if (HAL_SPI_Init(&hspi1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN SPI1_Init 2 */

  /* USER CODE END SPI1_Init 2 */

}

/**
  * @brief TIM3 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM3_Init(void)
{

  /* USER CODE BEGIN TIM3_Init 0 */

  /* USER CODE END TIM3_Init 0 */

  TIM_ClockConfigTypeDef sClockSourceConfig = {0};
  TIM_MasterConfigTypeDef sMasterConfig = {0};

  /* USER CODE BEGIN TIM3_Init 1 */

  /* USER CODE END TIM3_Init 1 */
  htim3.Instance = TIM3;
  htim3.Init.Prescaler = 47999;
  htim3.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim3.Init.Period = 999;
  htim3.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim3.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_Base_Init(&htim3) != HAL_OK)
  {
    Error_Handler();
  }
  sClockSourceConfig.ClockSource = TIM_CLOCKSOURCE_INTERNAL;
  if (HAL_TIM_ConfigClockSource(&htim3, &sClockSourceConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim3, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM3_Init 2 */

  /* USER CODE END TIM3_Init 2 */

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
  __disable_irq();
  while (1)
  {
  }
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
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
