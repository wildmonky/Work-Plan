import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_a01776b1df95fcc13ebceba4f6d30c1a
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "1".equals(mapRow.get("MODIFY_FLAG_YEAR"))?"<font color=red>"+mapRow.get("YEAR_OCS_DL")+"</font>":mapRow.get("YEAR_OCS_DL");
    }
}
